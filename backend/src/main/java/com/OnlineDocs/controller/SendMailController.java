package com.OnlineDocs.controller;

import com.OnlineDocs.entity.UserInfo;
import com.OnlineDocs.service.CooperateService;
import com.OnlineDocs.service.DocumentService;
import com.OnlineDocs.service.UserInfoService;
import com.OnlineDocs.utils.Constants;
import com.OnlineDocs.utils.MailUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

@Controller
public class SendMailController {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Configuration configuration;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    private CooperateService cooperateService;

    @Autowired
    private DocumentService documentService;

    @RequestMapping("/mail/send-invite")
    @ResponseBody
    public String sendInvitationEmail(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "invitelist") List<String> invite_list,
            @RequestParam(value = "invitelink") String invite_link,
            @RequestParam(value = "privilege") String privilege
    ) {
        HashMap<String,Object> map = new HashMap<String,Object>();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        String result = "fail";
        MailUtil emailUtils = new MailUtil();
        String mailSubject = "Online Docs 文档邀请";

        List<UserInfo> user = userInfoService.getUserByName(username);


        String AttachmentLocation =documentService.getDocByDocID(cooperateService.getCoopbyUserIDandAuth(
                "own",user.get(0).getIduser()
        ).get(0).getDocumentIddocument()).get(0).getTextPath();

        for (String invite_user:invite_list) {

            String sendTo = userInfoService.getUserByName(invite_user).get(0).getUserEmail();
            String[] invitation_info = {username, invite_user, invite_link, privilege};
            result=emailUtils.sendMailFreeMarker(mailSubject, AttachmentLocation, sendTo, javaMailSender, configuration,
                    Constants.INVITATION_EMAIL, invitation_info);
        }

        map.put("message",result);
        try {
            json = mapper.writeValueAsString(map);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
