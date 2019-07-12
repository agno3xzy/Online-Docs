package com.OnlineDocs.controller;

import com.OnlineDocs.entity.UserInfo;
import com.OnlineDocs.service.UserInfoService;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class RegisterController {
    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping(value = "/register/checkUsername")
    @ResponseBody
    public String checkUsername(@RequestParam(value = "username") String username) {
        HashMap<String,Object> map = new HashMap<String,Object>();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        String result = "";
        List<UserInfo> account = userInfoService.getUserByName(username);

        if (account.size()==0){
            result="true";
        }
        else{
            result="false";
        }
        map.put("message",result);

        try {
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @RequestMapping(value = "/register/create")
    @ResponseBody
    public String register(@RequestParam(value = "username") String username,
                            @RequestParam(value = "password") String password,
                            @RequestParam(value = "email") String email){
        HashMap<String,Object> map = new HashMap<String,Object>();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        String result = "";
        List<UserInfo> account = userInfoService.getUserByName(username);

        //获取项目真实路径
        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        ServletContext servletContext = webApplicationContext.getServletContext();
        String realPath = servletContext.getRealPath("/fileUpload");

        //String realPath = "/home/onlineDocs/fileUpload";

        char sep=LineSeperatorUtil.getLineSeperator();

        File create=new File(realPath+ sep +username+sep+"create");
        //File coop=new File(path+"\\"+username+"\\coop");
        File log=new File(realPath+sep+username+sep+"log");
        //System.out.println(account.size());

        if(userInfoService.addUser(username, password, email))
        {
            create.mkdirs();
            //coop.mkdirs();
            log.mkdirs();

            result="true";
        }
        else{
            result="false";
        }

        map.put("isValidate",result);

        try {
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
