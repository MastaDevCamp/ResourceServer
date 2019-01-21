package com.smilegate.masta.resource.service;


import com.smilegate.masta.resource.util.DefaultRes;
import com.smilegate.masta.resource.util.ResponseMessage;
import com.smilegate.masta.resource.util.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;


@Slf4j
@Service
public class JGitService {

    @Value("${localResourceFile.path}")
    private String localPath;

    @Value("${remouteResourceFile.path}")
    private String remotePath;

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

    public DefaultRes ConnectionRemoteRepository() throws IOException,GitAPIException{

        final Map<String, Ref> map = Git.lsRemoteRepository()
                .setHeads(true)
                .setTags(true)
                .setRemote(remotePath)
                .callAsMap();

        System.out.println("As map");
        for (Map.Entry<String, Ref> entry : map.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Ref: " + entry.getValue());
        }
        log.info("connection success");
        return DefaultRes.res(StatusCode.OK, ResponseMessage.CONNECTION_SUCCESS);
    }


    public DefaultRes showGitLog() throws IOException, GitAPIException{
        StringBuilder stringBuilder = new StringBuilder();
        try(Repository repository = ConnectionLocalRepository()){
            try(Git git = new Git(repository)){
                Iterable<RevCommit> logs = git.log()
                        .call();
                int count =0;
                for (RevCommit rev : logs) {
                    System.out.println("Commit: " + rev  + ", name: " + rev.getName() + ", id: " + rev.getId().getName() );
                    stringBuilder.append("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */+"\n");
                    count++;
                }
                System.out.println("Had " + count + " commits overall on current branch");
                stringBuilder.append("Had " + count + " commits overall on current branch");
                return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_GIT_LOG, stringBuilder);
            }
        }
    }

}
