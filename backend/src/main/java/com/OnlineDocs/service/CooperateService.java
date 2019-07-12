package com.OnlineDocs.service;

import com.OnlineDocs.dao.CooperateMapper;
import com.OnlineDocs.entity.Cooperate;
import org.springframework.beans.factory.annotation.Autowired;
import com.OnlineDocs.entity.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("CooperateService")
public interface CooperateService {
    Integer deleteCooperatebyDocumentID(Integer documentID);
    Integer addCooperate(String authority,Integer userID,Integer documentID);
    List<Cooperate> getCoopbyDocIDandAuth(String authority,Integer documentID);
    List<Cooperate> getCoopbyUserIDandAuth(String authority, Integer userID);
    List<Cooperate> getCoopbyUserID(Integer userID);
    List<Cooperate> getCoopbyUserIDandyDocID(Integer documentID,Integer userID);
    List<Cooperate> getCoopByDocID(Integer documentID);
    String updateCooperate(int docID,int userID,String curAuth,String ChangedAuth);
    List<Cooperate> getCoopByDocIDandAuthWithoutRead(Integer documentID);
}