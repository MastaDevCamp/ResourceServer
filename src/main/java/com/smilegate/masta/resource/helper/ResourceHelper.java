package com.smilegate.masta.resource.helper;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class ResourceHelper {

    public static Repository openJGitResourceRepository() throws IOException{
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder
                .readEnvironment() //scan environment GIT_* variables
                .findGitDir() //scan up the file system tree
                .build();
    }

    public static Repository creatNewRepository() throws IOException{
        //prepare a new folder
        File localPath = File.createTempFile("TestGitRepository", "");
        if(!localPath.delete()){
            throw new IOException("Could not delete temporary file " + localPath);
        }

        //create the directory
        Repository repository = FileRepositoryBuilder.create(new File(localPath, ".git"));
        repository.create();

        return repository;
    }
}
