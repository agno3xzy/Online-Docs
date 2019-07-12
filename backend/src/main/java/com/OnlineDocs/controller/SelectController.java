package com.OnlineDocs.controller;

import com.OnlineDocs.entity.Cooperate;
import com.OnlineDocs.entity.Document;
import com.OnlineDocs.entity.UserInfo;
import com.OnlineDocs.file.SaveEditVersion;
import com.OnlineDocs.service.CooperateService;
import com.OnlineDocs.service.DocumentService;
import com.OnlineDocs.service.UserInfoService;
import com.OnlineDocs.utils.CommonOperation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class SelectController {

    @Autowired
    DocumentService documentService;

    @Autowired
    CooperateService cooperateService;

    @Autowired
    UserInfoService userInfoService;

    @RequestMapping(value = "/selectDoc")
    @ResponseBody
    public String selectDoc(@RequestParam(value = "keyword") String keyword,
                                 @RequestParam(value = "username") String username) {

        HashMap<String,Object> map = new HashMap<String,Object>();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        String result="";
        List<HashMap<String,Object>> doclist = new ArrayList<>();

        if(!"".equals(keyword))
        {
            List<UserInfo> user = userInfoService.getUserByName(username);

            List<Document> docList = documentService.getDocmentbyKeyword(keyword,user.get(0).getIduser());

            for (Document doc:docList){
                HashMap<String,Object> docmap = new HashMap<String,Object>();

                List<Cooperate> c=cooperateService.getCoopbyUserIDandyDocID(doc.getIddocument(),user.get(0).getIduser());

                docmap.put("auth",c.get(0).getPermission());

                docmap.put("docName",doc.getDocumentName());

                docmap.put("lastModifyTime", doc.getLastModifyTime());

                docmap.put("oldPath", doc.getTextPath());

                docmap.put("newPath", CommonOperation.getNewFilePathbyOldFilePath(doc.getTextPath()));

                doclist.add(docmap);
            }

        }
        map.put("docList",doclist);
        //将全部数据的HashMap转为json
        try {
            json = mapper.writeValueAsString(map);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return json;
    }
}
