package com.smilegate.masta.resource.api;

import com.smilegate.masta.resource.helper.ResourceHelper;
import com.smilegate.masta.resource.service.JGitService;
import io.swagger.annotations.Api;
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

    @GetMapping("/log")
    public ResponseEntity showGitLog() throws IOException, GitAPIException{
        return new ResponseEntity(jGitService.showGitLog(), HttpStatus.OK);
    }

    @GetMapping("/diff")
    public ResponseEntity showGitDiff() throws IOException, GitAPIException{
        return null;
    }



}
