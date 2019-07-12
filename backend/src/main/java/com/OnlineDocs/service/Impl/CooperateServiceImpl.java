package com.OnlineDocs.service.Impl;

import com.OnlineDocs.dao.CooperateMapper;
import com.OnlineDocs.entity.Cooperate;
import com.OnlineDocs.entity.UserInfo;
import com.OnlineDocs.service.CooperateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

public class CooperateServiceImpl implements CooperateService {

    @Autowired
    CooperateMapper cooperateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteCooperatebyDocumentID(Integer documentID) {
        return cooperateMapper.deleteCooperatebyDocumentID(documentID);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addCooperate(String authority, Integer userID, Integer documentID) {
        return cooperateMapper.addCooperate(authority, userID, documentID);
    }

    @Override
    public List<Cooperate> getCoopbyDocIDandAuth(String authority, Integer documentID) {
        Example example = new Example(Cooperate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("permission", authority);
        criteria.andEqualTo("documentIddocument", documentID);
        return cooperateMapper.selectByExample(example);
    }

    @Override
    public List<Cooperate> getCoopbyUserIDandAuth(String authority, Integer userID) {
        Example example = new Example(Cooperate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("permission", authority);
        criteria.andEqualTo("userIduser", userID);
        return cooperateMapper.selectByExample(example);
    }

    @Override
    public List<Cooperate> getCoopbyUserID(Integer userID) {
        Example example = new Example(Cooperate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userIduser", userID);
        return cooperateMapper.selectByExample(example);
    }

    @Override
    public List<Cooperate> getCoopbyUserIDandyDocID(Integer documentID, Integer userID) {
        Example example = new Example(Cooperate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userIduser", userID);
        criteria.andEqualTo("documentIddocument", documentID);
        return cooperateMapper.selectByExample(example);
    }

    @Override
    public List<Cooperate> getCoopByDocID(Integer documentID) {
        Example example = new Example(Cooperate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("documentIddocument", documentID);
        return cooperateMapper.selectByExample(example);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateCooperate(int docID, int userID, String curAuth, String changedAuth) {
        return cooperateMapper.updateCooperate(docID, userID, curAuth, changedAuth);
    }

    @Override
    public List<Cooperate> getCoopByDocIDandAuthWithoutRead(Integer documentID) {
        Example example = new Example(Cooperate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("documentIddocument", documentID);
        criteria.andNotEqualTo("permission", "read");
        return cooperateMapper.selectByExample(example);
    }

}
