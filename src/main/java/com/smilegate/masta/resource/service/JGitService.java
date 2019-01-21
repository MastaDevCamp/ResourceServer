package com.smilegate.masta.resource.service;


import com.smilegate.masta.resource.model.CommitData;
import com.smilegate.masta.resource.util.DefaultRes;
import com.smilegate.masta.resource.util.ResponseMessage;
import com.smilegate.masta.resource.util.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class JGitService {

    @Value("${localResourceFile.path}")
    private String localPath;

    @Value("${remouteResourceFile.path}")
    private String remotePath;


    /**
     *
     * @return local Repository의 .git 파일을 Repository 타입으로 return
     * @throws IOException
     * @throws GitAPIException
     */

    public Repository ConnectionLocalRepository() throws IOException, GitAPIException {

        File repoDir = new File(localPath);
        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        //open an existing repository (by setGitDir method)
        try(Repository repository = builder.setGitDir(repoDir)
                    .readEnvironment()
                    .findGitDir()
                    .build()) {

            log.info("Having repository: " + repository.getDirectory());
            Ref head = repository.exactRef("refs/heads/master");
            log.info("Ref of refs/heads/master: " + head);

            return repository;
        }
    }


    /**
     *
     * @return 두가지 git Commit의 difference를 Response 형식에 맞춰서 return
     * @throws IOException
     * @throws GitAPIException
     */
    public DefaultRes showGitDiff() throws IOException, GitAPIException{
        try (Repository repository = ConnectionLocalRepository()) {
            // the diff works on TreeIterators, we prepare two for the two branches
            AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, "b97b184b0ce11c0b6a4dcc2b57768ff155cb696b");
            AbstractTreeIterator newTreeParser = prepareTreeParser(repository, "9e0719d7d773b41b49ebf04e6fd7b5c637e96063");

            // then the porcelain diff-command returns a list of diff entries
            try (Git git = new Git(repository)) {
                List<DiffEntry> diff = git.diff().
                        setOldTree(oldTreeParser).
                        setNewTree(newTreeParser).
                        setPathFilter(PathFilter.create("README.md")).
                        // to filter on Suffix use the following instead
                        //setPathFilter(PathSuffixFilter.create(".java")).
                                call();
                for (DiffEntry entry : diff) {
                    System.out.println("Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId());
                    try (DiffFormatter formatter = new DiffFormatter(System.out)) {
                        formatter.setRepository(repository);
                        formatter.format(entry);
                    }
                }
            }
        }
        return null;
    }


    private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }
            walk.dispose();
            return treeParser;
        }
    }

    /**
     *
     * commit list = git log
     *
     * @return local에 있는 저장소와 연결된 remote저장소의 log를 return한다.
     *          : 해당 API 호출시마다, git status이용해서 remote update여부 확인후, remote update cammand 사용 예정
     * @throws IOException
     * @throws GitAPIException
     */

    public DefaultRes showGitLog() throws IOException, GitAPIException {
        try(Repository repository = ConnectionLocalRepository()){
            try(Git git = new Git(repository)){

                List<CommitData> commitDataList = new ArrayList<>();

                /*

                //local commit list
                Iterable<RevCommit> logs = git.log()
                        .call();
                int count =0;
                for (RevCommit rev : logs) {
                    log.info("Commit: " + rev  + ", name: " + rev.getName() + ", id: " + rev.getId().getName() + ", short message:" + rev.getShortMessage() + ", long message:" + rev.getFullMessage() );
                    count++;
                }
                log.info("Had " + count + " commits overall on local master");

                */


                //remote commit list
                System.out.println("=====");
                log.info("commit branch : /remotes/origin/master");
                Iterable<RevCommit> logs = git.log()
                        .add(repository.resolve("remotes/origin/master"))
                        .call();
                int count = 0;
                for (RevCommit rev : logs) {

                    log.info(rev.toString());
                    log.info("Commit id : " + rev.getId() + " Commit short message : " + rev.getShortMessage() + " Commit Time : " + rev.getCommitTime());
                    CommitData commitData = new CommitData(rev.getId().toString() , rev.getShortMessage(), new Timestamp(rev.getCommitTime()) );
                    commitDataList.add(commitData);
                    count++;
                }

                log.info("Had " + count + " commits overall on origin/master");

                return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_GIT_LOG, commitDataList);
            }
        }
    }



    /**
     *          git status를 이용해서 현재 git 저장소의 local과 remote의 pull, push 여부를 boolean과 statusCode로 표기
     * @throws IOException
     * @throws GitAPIException
     */
    public void gitStatus() throws IOException, GitAPIException{
        StringBuilder stringBuilder = new StringBuilder();
        try (Repository repository = ConnectionLocalRepository()) {
            try (Git git = new Git(repository)) {
                Status status = git.status().call();
                stringBuilder.append("Added: " + status.getAdded() +"\n");
                stringBuilder.append("Changed: " + status.getChanged() +"\n");
                stringBuilder.append("Conflicting: " + status.getConflicting() +"\n");
                stringBuilder.append("ConflictingStageState: " + status.getConflictingStageState() +"\n");
                stringBuilder.append("IgnoredNotInIndex: " + status.getIgnoredNotInIndex() +"\n");
                stringBuilder.append("Missing: " + status.getMissing() +"\n");
                stringBuilder.append("Modified: " + status.getModified() +"\n");
                stringBuilder.append("Removed: " + status.getRemoved() +"\n");
                stringBuilder.append("Untracked: " + status.getUntracked() +"\n");
                stringBuilder.append("UntrackedFolders: " + status.getUntrackedFolders() +"\n");
                stringBuilder.toString();
            }
        }
    }

}
