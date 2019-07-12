package com.OnlineDocs.controller;


import com.OnlineDocs.entity.*;
import com.OnlineDocs.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.util.*;

@Controller
public class CheckController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private InviteInfoService inviteInfoService;

    @Autowired
    private CooperateService cooperateService;

    @RequestMapping(value = "/invite/checkDocId")
    @ResponseBody
    public String checkDocId(
            @RequestParam(value = "docID", required = false) int docID
    ) {
        List<InviteInfo> inviteInfoList = inviteInfoService.getLatestInviteByDocID(docID);
        if (inviteInfoList.isEmpty()) {
            return "{\"message\":\"false\"}";

        } else {
            Date latestInviteInfo = inviteInfoList.get(0).getInviteTime();
            int inviteID = inviteInfoList.get(0).getInviteId();
            Date curTime = new Date();
            int days = (int) ((curTime.getTime() - latestInviteInfo.getTime()) / (1000 * 60 * 60 * 24));
            if (days < 3) {
                return "{\"message\":\"success\"}";
            } else {
                String deleteRes = inviteInfoService.deleteInviteByInviteID(inviteID);
                return "{\"message\":\"false\"}";
            }


        }


    }

    @RequestMapping(value = "/invite/checkUser")
    @ResponseBody
    public String checkUser(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "docID", required = false) int docID,
            @RequestParam(value = "auth", required = false) String auth
    ) {
        HashMap<String,Object> map = new HashMap<String,Object>();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        String result="";
        List<UserInfo> account = userInfoService.getUserByName(username);
        int userID = account.get(0).getIduser();
        List<Cooperate> cooperateList = cooperateService.getCoopbyUserIDandyDocID(docID,account.get(0).getIduser());
        System.out.println(account.size());
        //判断用户名密码是否有效
        if (account.size() == 0) {
            result = "userError";
        }
        else
        {
            if (account.get(0).getPassword().equals(password)) {
                List<InviteInfo> inviteInfoList = inviteInfoService.getInviteByDocIDUserIDAuth(docID,userID, auth);
                if(inviteInfoList.size()==0)
                {
                    result = "docError";
                }
                else
                {
                    if(cooperateList.size()==0)
                    {
                        cooperateService.addCooperate(auth,userID,docID);
                        result = "success";
                    }
                    else
                    {
                        String curAuth = cooperateList.get(0).getPermission();
                        if("read".equals(curAuth))
                        {
                            if(auth.equals("read"))
                                result = "authWrong";
                            else if(auth.equals("share"))
                            {
                                cooperateService.updateCooperate(docID,userID,curAuth,auth);
                                result = "success";
                            }

                        }
                        else if("share".equals(curAuth))
                        {
                            result = "authWrong";
                        }

                    }
                    result = "success";
                }

            }
            else
            {
                result = "userError";
            }
            map.put("email", account.get(0).getUserEmail());
        }
        map.put("message",result);
        //将全部数据的HashMap转为json
        {
            try {
                json = mapper.writeValueAsString(map);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return json;
    }

}
