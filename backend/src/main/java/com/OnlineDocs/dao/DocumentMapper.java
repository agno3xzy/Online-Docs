package com.OnlineDocs.dao;

import com.OnlineDocs.entity.Document;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface DocumentMapper extends Mapper<Document> {

    Integer getDocumentIDbyFilePath(@Param("filePath") String filePath);

    Integer deleteDocumentbyID(@Param("documentID") Integer documentID);

    Integer addDocument(@Param("documentName") String documentName,@Param("createTime") Date createTime,
                        @Param("lastModifyTime") Date lastModifyTime,@Param("filePath") String filePath,
                        @Param("logPath") String logPath);
}
