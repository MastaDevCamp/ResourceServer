package com.smilegate.masta.resource.service;

import com.smilegate.masta.resource.dto.Version;
import com.smilegate.masta.resource.mapper.VersionMapper;
import com.smilegate.masta.resource.util.DefaultRes;
import com.smilegate.masta.resource.util.ResponseMessage;
import com.smilegate.masta.resource.util.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VersionService {

    private final VersionMapper versionMapper;

    public VersionService(final VersionMapper versionMapper) {
        this.versionMapper = versionMapper;
    }

    public DefaultRes findByCommitID(final String commitID) {
        final Version version = versionMapper.findByCommitID(commitID);
        if (version == null)
            return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NO_COMMITID);
        return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_COMMITID, version);
    }
}
