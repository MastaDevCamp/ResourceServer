package com.smilegate.masta.resource.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
public class CommitData {
    private String commitId;
    private String commitMessage;
    private Timestamp commitTime;
}
