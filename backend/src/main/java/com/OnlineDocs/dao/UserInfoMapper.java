package com.OnlineDocs.dao;

import com.OnlineDocs.entity.UserInfo;
import com.sun.org.glassfish.gmbal.ParameterNames;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface UserInfoMapper extends Mapper<UserInfo> {
    Integer addUserInfo(@Param("username") String username,@Param("password") String password, @Param("email") String email);
    Integer modifyKey(@Param("username") String username,@Param("password") String password);
}