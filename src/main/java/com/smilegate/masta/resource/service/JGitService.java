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
}
