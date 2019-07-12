package com.OnlineDocs.service;

import com.OnlineDocs.entity.Document;
import com.OnlineDocs.entity.InviteInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;

@Service("InviteInfoService")
public interface InviteInfoService {

    List<InviteInfo> getLatestInviteByDocID(int docID);

    List<InviteInfo> getInviteByDocID(int docID);

    String deleteInviteByInviteID(int inviteID);

    List<InviteInfo> getInviteByDocIDUserIDAuth(int docID,int userID,String authority);

    String insertInviteInfo(int docID,String auth,Date inviteTime,int userID);
}
