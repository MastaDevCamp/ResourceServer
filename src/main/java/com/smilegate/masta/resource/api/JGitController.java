package com.smilegate.masta.resource.api;

import com.smilegate.masta.resource.helper.ResourceHelper;
import com.smilegate.masta.resource.service.JGitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController


@RequestMapping("git")
@Api(value = "jGit", description = "jGit controller")
public class JGitController
{

    private JGitService jGitService;

    public JGitController(final JGitService jGitService){
        this.jGitService = jGitService;
    }



    @GetMapping("/diff")
    public ResponseEntity showGitDiff() throws IOException, GitAPIException{
        return null;
    }


    @GetMapping("/status")
    @ApiOperation(value = "git 페이지 : status 변경사항 보기 페이지")
    //status 변경사항 보기!! > 그냥 string으로 반환했음 client에서 힘들듯!! (testing)
    @SuppressWarnings("unused")

    public ResponseEntity gitStatus() throws IOException, GitAPIException{
        StringBuilder stringBuilder = new StringBuilder();
        try (Repository repository = jGitService.ConnectionRepository()) {
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
        return new ResponseEntity(stringBuilder, HttpStatus.OK);
    }


    //안쓰는 api
    @SuppressWarnings("unused")
    @GetMapping("")
    public void gitAPI() throws IOException, GitAPIException {
        File repoDir = new File("C:/Users/user/Desktop/smilegate/resourceFile/.git");

        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        //open an existing repository (by setGitDir method)
        try(Repository repository = builder.setGitDir(repoDir)
                .readEnvironment()
                .findGitDir()
                .build()){
            log.info("Having repository: " + repository.getDirectory() +"\n");

            // the Ref holds an ObjectId for any type of object (tree, commit, blob, tree)
            Ref head = repository.exactRef("refs/heads/master");
            log.info("Ref of refs/heads/master: " + head);
        }
    }
}
