package com.OnlineDocs.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MailUtil {


    /**
     * Text或者HTML格式邮件的方法
     *
     * @param text           要发送的内容
     * @param subject        邮件的主题也就是邮件的标题
     * @param emailAdress    目的地
     * @param javaMailSender 发送邮件的核心类（在xml文件中已经配置好了）
     * @param type           如果为true则代表发送HTML格式的文本
     * @param location       发送附件的地址，若无附件则接收字符串false
     * @return
     */
    public String sendMail(String text, String subject, String emailAdress,
                           JavaMailSender javaMailSender, Boolean type, String location) {

        MimeMessage mMessage = javaMailSender.createMimeMessage();// 创建邮件对象
        MimeMessageHelper mMessageHelper;
        Properties prop = new Properties();
        try {
            // 从配置文件中拿到发件人邮箱地址
            prop.load(this.getClass().getResourceAsStream("/mail.properties"));
            String from = prop.get("mail.smtp.username") + "";
            mMessageHelper = new MimeMessageHelper(mMessage, true, "UTF-8");
            // 发件人邮箱
            mMessageHelper.setFrom(from);
            // 收件人邮箱
            mMessageHelper.setTo(emailAdress);
            // 邮件的主题也就是邮件的标题
            mMessageHelper.setSubject(subject);
            // 邮件的文本内容，true表示文本以html格式打开
            if (type) {
                mMessageHelper.setText(text, true);
            } else {
                mMessageHelper.setText(text, false);
            }
            // 在邮件中添加一个附件

            if (!location.equals("false")) {
                // 通过文件路径获取文件名字
                String filename = StringUtils.getFilename(location);
                // 定义要发送的资源位置
                File file = new File(location);
                FileSystemResource resource = new FileSystemResource(file);
                mMessageHelper.addAttachment(filename, resource);// 在邮件中添加一个附件
            }
            javaMailSender.send(mMessage);// 发送邮件
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

    /**
     * FreeMarker模板格式的邮件的方法
     *
     * @param subject                 邮件的主题也就是邮件的标题
     * @param location                文件的地址
     * @param emailAdress             目的地
     * @param javaMailSender          发送邮件的核心类
     * @param freeMarkerConfiguration freemarker配置管理类
     * @param type                    邮件类型
     * @return
     * @throws TemplateException
     */

    public String sendMailFreeMarker(String subject, String location, String emailAdress, JavaMailSender javaMailSender,
                                     Configuration freeMarkerConfiguration, Integer type,
                                     String[] invitation_info) {
        MimeMessage mMessage = javaMailSender.createMimeMessage();// 创建邮件对象
        MimeMessageHelper mMessageHelper;
        Properties prop = new Properties();
        try {
            // 从配置文件中拿到发件人邮箱地址
            prop.load(this.getClass().getResourceAsStream("/mail.properties"));
            String from = prop.get("mail.smtp.username") + "";

            mMessageHelper = new MimeMessageHelper(mMessage, true);
            mMessageHelper.setFrom(from);
            mMessageHelper.setTo(emailAdress);
            mMessageHelper.setSubject(subject);

            switch (type) {
                case Constants.INVITATION_EMAIL:
                    mMessageHelper.setText(getInviteText(freeMarkerConfiguration,invitation_info[0],
                            invitation_info[1],invitation_info[2],invitation_info[3]), true);
                    break;
                case Constants.NOTIFICATION_EMAIL:

                    break;

                default: //可选
                    //语句
            }


            String filename = StringUtils.getFilename(location);
            if (!location.equals("false")) {
                // 定义要发送的资源位置
                File file = new File(location);
                FileSystemResource resource = new FileSystemResource(file);
                mMessageHelper.addAttachment(filename, resource);// 在邮件中添加一个附件
            }
            javaMailSender.send(mMessage);// 发送邮件
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

    /**
     * 邀请链接模版
     *
     * @param user        邀请者
     * @param invite_user 受邀者
     * @param invite_link 邀请链接
     * @param privilege   权限
     */
    private String getInviteText(Configuration freeMarkerConfiguration, String user, String invite_user,
                                 String invite_link, String privilege) {
        String txt = "";
        try {
            Template template = freeMarkerConfiguration.getTemplate("/invitationemail.ftl");


            Map<String, Object> map = new HashMap<String, Object>();
            map.put("user", user);
            map.put("invite_user", invite_user);
            map.put("invite_link", invite_link);
            map.put("privilege", privilege);

            txt = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            System.out.println("getText()->>>>>>>>>");
            System.out.println(txt);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        return txt;
    }


}
