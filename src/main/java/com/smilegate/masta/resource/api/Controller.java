package com.smilegate.masta.resource.api;

import com.smilegate.masta.resource.helper.ResourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("git")
public class Controller
{
    @GetMapping("")
    public void gitAPI() throws IOException, GitAPIException {
//        File repoDir = createSampleGitRepo();

//        File repoDir = new File("C:\\Users\\user\\Desktop\\smilegate\\resourceFile")
        File repoDir = new File("C:/Users/user/Desktop/smilegate/resourceFile/.git");

        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        //open an existing repository (by setGitDir method)
        try(Repository repository = builder.setGitDir(repoDir)
                .readEnvironment()
                .findGitDir()
                .build()){
            log.info("Having repository: " + repository.getDirectory());

            // the Ref holds an ObjectId for any type of object (tree, commit, blob, tree)
            Ref head = repository.exactRef("refs/heads/master");
            log.info("Ref of refs/heads/master: " + head);
        }

        // clean up here to not keep using more and more disk-space for these samples
        FileUtils.deleteDirectory(repoDir.getParentFile());

    }
}
