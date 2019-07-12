package com.OnlineDocs.controller;

import com.OnlineDocs.entity.Cooperate;
import com.OnlineDocs.entity.Document;
import com.OnlineDocs.entity.UserInfo;
import com.OnlineDocs.service.CooperateService;
import com.OnlineDocs.service.DocumentService;
import com.OnlineDocs.service.UserInfoService;
import com.OnlineDocs.utils.CommonOperation;
import com.OnlineDocs.utils.LineSeperatorUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ShowDocsController {
    @Autowired
    DocumentService documentService;

    @Autowired
    CooperateService cooperateService;

    @Autowired
    UserInfoService userInfoService;


    @RequestMapping(value = "/document-manage/create")
    @ResponseBody
    public String createFile(@RequestParam(value = "username") String username) {
        HashMap<String,Object> map = new HashMap<String,Object>();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";

        List<UserInfo> user= userInfoService.getUserByName(username);

        List<Cooperate> coopCreateList=cooperateService.getCoopbyUserIDandAuth("own",user.get(0).getIduser());

        List<HashMap<String,Object>> docItems=new ArrayList<>();

        for (Cooperate coop:coopCreateList){
            HashMap<String,Object> docItem=new HashMap<String,Object>();
            List<Document> doc=documentService.getDocByDocID(coop.getDocumentIddocument());

            docItem.put("docName",doc.get(0).getDocumentName());
            docItem.put("lastUseTime",doc.get(0).getLastModifyTime());

            char sep= LineSeperatorUtil.getLineSeperator();
            docItem.put("oldPath",doc.get(0).getTextPath().replace('/',sep));
            docItem.put("newPath",CommonOperation.getNewFilePathbyOldFilePath(
                    doc.get(0).getTextPath().replace('/',sep)));

            List<Cooperate> coopt=cooperateService.getCoopbyDocIDandAuth("share",doc.get(0).getIddocument());

            List<String> shareUserList=new ArrayList<>();
            int shareAmount=0;
            for (Cooperate c:coopt){
                List<UserInfo> userShare=userInfoService.getUserByID(c.getUserIduser());
                shareUserList.add(userShare.get(0).getUserName());
                shareAmount++;
            }

            docItem.put("shareAmount",shareAmount);
            docItem.put("userList",shareUserList);

            docItems.add(docItem);
        }

        map.put("docList",docItems);

        try {
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @RequestMapping(value = "/document-manage/share")
    @ResponseBody
    public String shareFile(@RequestParam(value = "username") String username) {
        HashMap<String,Object> map = new HashMap<String,Object>();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";

        List<UserInfo> user= userInfoService.getUserByName(username);

        String[] auth={"share","read"};
        List<HashMap<String,Object>> docItems=new ArrayList<>();

        for (String a:auth){
            List<Cooperate> coopList=cooperateService.getCoopbyUserIDandAuth(a,user.get(0).getIduser());

            for (Cooperate coop:coopList){

                HashMap<String,Object> docItem=new HashMap<String,Object>();
                List<Document> doc=documentService.getDocByDocID(coop.getDocumentIddocument());

                docItem.put("docName",doc.get(0).getDocumentName());
                docItem.put("lastUseTime",doc.get(0).getLastModifyTime());

                char sep= LineSeperatorUtil.getLineSeperator();
                docItem.put("oldPath",doc.get(0).getTextPath().replace('/',sep));
                docItem.put("newPath",CommonOperation.getNewFilePathbyOldFilePath(
                        doc.get(0).getTextPath().replace('/',sep)));

                List<Cooperate> coopt=cooperateService.getCoopbyDocIDandAuth("own",doc.get(0).getIddocument());
                List<UserInfo> userCreate=userInfoService.getUserByID(coopt.get(0).getUserIduser());

                docItem.put("owner",userCreate.get(0).getUserName());
                docItem.put("auth",a);

                docItems.add(docItem);
            }
        }

        map.put("docList",docItems);
        try {
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @RequestMapping(value = "/document-manage/lastuse")
    @ResponseBody
    public String lastUse(@RequestParam(value = "username") String username) {
        HashMap<String,Object> map = new HashMap<String,Object>();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";

        List<UserInfo> user= userInfoService.getUserByName(username);

        Date date = new Date();
        Date endTime=date;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -15);
        Date startTime=calendar.getTime();

        List<Document> docList=documentService.orderDocIDbyLastModifyTime(user.get(0).getIduser(),startTime,endTime);
        List<HashMap<String,Object>> docItems=new ArrayList<>();

        for (Document doc:docList){
            HashMap<String,Object> docItem=new HashMap<String,Object>();

            docItem.put("docName",doc.getDocumentName());
            docItem.put("lastUseTime",doc.getLastModifyTime());

            char sep= LineSeperatorUtil.getLineSeperator();
            docItem.put("oldPath",doc.getTextPath().replace('/',sep));
            docItem.put("newPath",CommonOperation.getNewFilePathbyOldFilePath(
                    doc.getTextPath().replace('/',sep)));

            List<Cooperate> coopOwner=cooperateService.getCoopbyDocIDandAuth("own",doc.getIddocument());
            List<UserInfo> userCreate=userInfoService.getUserByID(coopOwner.get(0).getUserIduser());
            List<Cooperate> cooptSelf=cooperateService.getCoopbyUserIDandyDocID(doc.getIddocument(),user.get(0).getIduser());

            docItem.put("owner",userCreate.get(0).getUserName());
            docItem.put("auth",cooptSelf.get(0).getPermission());

            docItems.add(docItem);
        }

        map.put("docList",docItems);
        try {
            json = mapper.writeValueAsString(map);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return json;
    }

}
