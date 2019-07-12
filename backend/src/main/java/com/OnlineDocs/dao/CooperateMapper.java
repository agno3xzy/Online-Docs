package com.OnlineDocs.dao;

import com.OnlineDocs.entity.Cooperate;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CooperateMapper extends Mapper<Cooperate> {
    Integer deleteCooperatebyDocumentID(@Param("documentID") int documentID);
    Integer addCooperate(@Param("authority")String authority,@Param("userID")Integer userID,@Param("documentID")Integer documentID);
    List<Object> selectDocIDbyUserID(@Param("userID") Integer userID);
    String updateCooperate(@Param("docID") int docID,@Param("userID")int userID,@Param("curAuth") String curAuth,@Param("changedAuth") String changedAuth);
}