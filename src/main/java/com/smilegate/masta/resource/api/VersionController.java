package com.smilegate.masta.resource.api;

import com.smilegate.masta.resource.service.VersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.smilegate.masta.resource.util.DefaultRes.FAIL_DEFAULT_RES;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("versions")
public class VersionController {

    private final VersionService versionService;

    public VersionController(final VersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping("commit")
    public ResponseEntity getVersion(
            @RequestParam("commitID") final Optional<String> commitID) {
        try {
            return new ResponseEntity<>(versionService.findByCommitID(commitID.get()), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
