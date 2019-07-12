package com.OnlineDocs.controller;

import com.OnlineDocs.dao.InviteInfoMapper;
import com.OnlineDocs.entity.Cooperate;
import com.OnlineDocs.entity.Document;
import com.OnlineDocs.entity.InviteInfo;
import com.OnlineDocs.entity.UserInfo;
import com.OnlineDocs.service.CooperateService;
import com.OnlineDocs.service.DocumentService;
import com.OnlineDocs.service.InviteInfoService;
import com.OnlineDocs.service.UserInfoService;
import com.OnlineDocs.utils.LineSeperatorUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.registry.infomodel.User;
import java.util.*;

@Controller

public class ShareController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private InviteInfoService inviteInfoService;

    @Autowired
    private CooperateService cooperateService;

    @RequestMapping(value = "/share/send")
    @ResponseBody
    public String send(@RequestParam(value = "username") String username,
                        @RequestParam(value = "docName") String docName) {
        HashMap<String,Object> map = new HashMap<String,Object>();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        int docID = documentService.getDocByDocName(docName).get(0).getIddocument();
        List<UserInfo> userList = userInfoService.getAllUser();
        List<InviteInfo> inviteList = inviteInfoService.getInviteByDocID(docID);
        List<Cooperate> coopList = cooperateService.getCoopByDocID(docID);
        List<String> hasInvitedAndCooperatedUsernameList = new ArrayList<>(); //已经发送邀请和已经合作的人
        List<String> usernameList = new ArrayList<>();
        for(int i = 0; i < inviteList.size(); i++)
        {
            String thisUsername = userInfoService.getUserByID(inviteList.get(i).getInviteUserId()).get(0).getUserName();
            hasInvitedAndCooperatedUsernameList.add(thisUsername);
        }
        for(int i = 0; i < coopList.size(); i++)
        {
            String thisUsername = userInfoService.getUserByID(coopList.get(i).getUserIduser()).get(0).getUserName();
            hasInvitedAndCooperatedUsernameList.add(thisUsername);
        }
        for(int i = 0; i < userList.size(); i++)
        {
            UserInfo user = userList.get(i);
            if(!hasInvitedAndCooperatedUsernameList.contains(user.getUserName()))
            {
                usernameList.add(user.getUserName());
            }
        }
        map.put("usernameList",usernameList);
        try {
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
    @RequestMapping(value = "/shareInfo")
    @ResponseBody
    public String share_info(@RequestParam(value = "userList", required = false) List<String> userList,
                             @RequestParam(value = "authority", required = false) String authority,
                             @RequestParam(value = "filePath", required = false) String filePath
    ) {

        int userID;
        int failTag = 0;
        char sep = LineSeperatorUtil.getLineSeperator();
        int docID = documentService.getDocumentbyFilePath(filePath.replace(sep,'/')).get(0).getIddocument();
        int userNum = userList.size();
        for(int i=0;i<userNum;i++)
        {
            userID = userInfoService.getUserByName(userList.get(i)).get(0).getIduser();

            String insertRes = inviteInfoService.insertInviteInfo(docID,authority,new Date(),userID);

            if(insertRes.equals("fail"))
                failTag = 1;

        }

        if(failTag==0)
            return "{\"message\":\"success\"}";
        else
            return "{\"message\":\"fail\"}";

    }











}
