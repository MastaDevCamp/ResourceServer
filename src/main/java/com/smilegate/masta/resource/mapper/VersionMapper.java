package com.smilegate.masta.resource.mapper;

import com.smilegate.masta.resource.dto.Version;
import org.apache.ibatis.annotations.*;

@Mapper
public interface VersionMapper {

    @Select("SELECT * FROM versiontable WHERE commitID = #{commitID}")
    Version findByCommitID(@Param("commitID") final String commitID);

}
