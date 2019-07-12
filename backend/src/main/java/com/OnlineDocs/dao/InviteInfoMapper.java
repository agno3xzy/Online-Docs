package com.OnlineDocs.dao;

import com.OnlineDocs.entity.InviteInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import java.util.Date;

public interface InviteInfoMapper extends Mapper<InviteInfo> {

    Integer insertInviteInfo(@Param("documentID") Integer documentID,
                             @Param("userID") Integer userID,
                             @Param("auth") String auth,
                             @Param("inviteTime") Date inviteTime
                        );
}