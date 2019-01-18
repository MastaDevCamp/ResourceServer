package com.smilegate.masta.resource.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class JGitService {

    @Value("${resourceFile.path}")
    private String localpath;

    public Repository ConnectionRepository() throws IOException, GitAPIException {

        File repoDir = new File(localpath);

        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        Repository repository = null;

        //open an existing repository (by setGitDir method)
        try{
            repository = builder.setGitDir(repoDir)
                    .readEnvironment()
                    .findGitDir()
                    .build();

            log.info("Having repository: " + repository.getDirectory());

            // the Ref holds an ObjectId for any type of object (tree, commit, blob, tree)
            Ref head = repository.exactRef("refs/heads/master");
            log.info("Ref of refs/heads/master: " + head);

        }catch (Exception e){
            log.error(e.getMessage());
        }

        // clean up here to not keep using more and more disk-space for these samples
//        FileUtils.deleteDirectory(repoDir.getParentFile());
        return repository;
    }

}
