package com.OnlineDocs.service;

import com.OnlineDocs.entity.Document;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import com.OnlineDocs.entity.Document;
import java.util.List;

@Service("DocumentService")
public interface DocumentService {

    List<Document> getDocmentbyKeyword(String keyword,Integer userID);
    List<Document> getDocumentbyFilePath(String filePath);
    List<Document> getDocByDocID(Integer docID);
    Integer deleteDocumentbyID(Integer documentID);
    Integer addDocument(String documentName,Date createTime,Date lastModifyTime,String filePath,String logPath);

    List<Document> orderDocIDbyLastModifyTime(Integer userID, Date startTime, Date endTime);
    List<Document> getDocByDocName(String docName);
}
