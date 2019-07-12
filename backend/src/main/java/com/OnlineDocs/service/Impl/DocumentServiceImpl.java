package com.OnlineDocs.service.Impl;

import com.OnlineDocs.dao.CooperateMapper;
import com.OnlineDocs.dao.DocumentMapper;
import com.OnlineDocs.entity.Document;
import com.OnlineDocs.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private CooperateMapper cooperateMapper;

    @Override
    public List<Document> getDocumentbyFilePath(String filePath) {
        Example example = new Example(Document.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("textPath", filePath);
        return documentMapper.selectByExample(example);
    }

    @Override
    public List<Document> getDocByDocID(Integer docID)
    {
        Example example = new Example(Document.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("iddocument",docID);
        return documentMapper.selectByExample(example);

    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer deleteDocumentbyID(Integer documentID) {
        return documentMapper.deleteDocumentbyID(documentID);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer addDocument(String documentName, Date createTime, Date lastModifyTime, String filePath, String logPath) {
        return documentMapper.addDocument(documentName,createTime,lastModifyTime,filePath,logPath);

    }

    @Override
    public List<Document> orderDocIDbyLastModifyTime(Integer userID,Date startTime,Date endTime){
        Example example = new Example(Document.class);
        Example.Criteria criteria = example.createCriteria();
        List<Object> coop = cooperateMapper.selectDocIDbyUserID(userID);
        if (coop.size() != 0){
            criteria.andIn("iddocument",cooperateMapper.selectDocIDbyUserID(userID));
            criteria.andBetween("lastModifyTime",startTime,endTime);
            example.setOrderByClause("lastModifyTime DESC");
            return documentMapper.selectByExample(example);
        }
        else{
            return new ArrayList<Document>();
        }
    }

    @Override
    public List<Document> getDocmentbyKeyword(String keyword,Integer userID){
        Example example = new Example(Document.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("iddocument",cooperateMapper.selectDocIDbyUserID(userID));
        criteria.andLike("documentName","%" + keyword + "%");
        return documentMapper.selectByExample(example);
    }


    @Override
    public List<Document> getDocByDocName(String docName)
    {
        Example example = new Example(Document.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("documentName",docName);
        return documentMapper.selectByExample(example);
    }
}
