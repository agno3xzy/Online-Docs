package com.OnlineDocs.controller;

import com.OnlineDocs.entity.Cooperate;
import com.OnlineDocs.file.FileList;
import com.OnlineDocs.file.FileOperation;
import com.OnlineDocs.utils.ColorUtil;
import com.OnlineDocs.utils.CommonOperation;
import com.OnlineDocs.utils.LineSeperatorUtil;
import com.OnlineDocs.utils.diff_match_patch;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import com.OnlineDocs.service.CooperateService;
import com.OnlineDocs.service.DocumentService;
import com.OnlineDocs.service.UserInfoService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

@Controller
public class VersionController {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private CooperateService cooperateService;

    /**
     * 添加 版本历史信息
     *
     * @param filePath String
     * @param userName String 单击文件保存按钮的操作者name
     * @return String
     */
    @RequestMapping(value = "/version-manage/add")
    @ResponseBody
    public String addVersion(@RequestParam(value = "username", required = false) String userName,
                             @RequestParam(value = "filepath", required = false) String filePath)
            throws IOException {

        return addVersion_atom(filePath);

    }

    public static String addVersion_atom(String filePath) throws IOException{
        HashMap<String, Object> map = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        String result = "";

        String timeStamp = CommonOperation.getTimeStamp();
        String versionLogPath = CommonOperation.getLogPath(filePath);
        String newFilePath = CommonOperation.getNewFilePathbyOldFilePath(filePath);

        File oldFile = new File(filePath);
        File newFile = new File(newFilePath);

        FileReader ofr = new FileReader(oldFile);  //字符输入流
        FileReader nfr = new FileReader(newFile);

        BufferedReader nbr = new BufferedReader(nfr);  //使文件可按行读取并具有缓冲功能
        BufferedReader obr = new BufferedReader(ofr);

        StringBuilder nsb = new StringBuilder();   //strB用来存储jsp.txt文件里的内容
        StringBuilder osb = new StringBuilder();

        String ostr = obr.readLine();
        String nstr = nbr.readLine();


        while (nstr != null) {
            nsb.append(nstr.replaceAll("\n", ""))
                    .append(System.getProperty("line.separator"));
            nstr = nbr.readLine();
        }

        while (ostr != null) {
            osb.append(ostr.replaceAll("\n", ""))
                    .append(System.getProperty("line.separator"));
            ostr = obr.readLine();
        }


        nbr.close();
        obr.close();

        ofr.close();
        nfr.close();

        diff_match_patch dmp = new diff_match_patch();
        LinkedList<diff_match_patch.Patch> patches = dmp.patch_make(nsb.toString(), osb.toString());
        String patch = dmp.patch_toText(patches);

        if (patch != null && !patch.equals("")) {
            Map<String, String> versionLog = new HashMap<String, String>();

            Gson gson = new Gson();

            File versionLogFile = new File(versionLogPath);

            if (versionLogFile.exists()) {
                Scanner scanner = new Scanner(versionLogFile);
                String content = scanner.useDelimiter("\\Z").next();
                scanner.close();
                Type type = new TypeToken<Map<String, String>>() {
                }.getType();
                versionLog = gson.fromJson(content, type);
            }

            versionLog.put(timeStamp, patch);
            PrintStream ps = new PrintStream(new FileOutputStream(versionLogFile));
            ps.print(gson.toJson(versionLog));
            ps.close();

            FileUtils.copyFile(newFile, oldFile);

        }

        map.put("message", "success");

        try {
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * 回退 版本历史信息
     *
     * @param filePath String
     * @param userName String 单击文件保存按钮的操作者id
     * @return String
     */
    @RequestMapping(value = "/version-manage/revert")
    @ResponseBody
    public String changeVersion(@RequestParam(value = "username", required = false) String userName,
                                @RequestParam(value = "filepath", required = false) String filePath,
                                @RequestParam(value = "timestamp", required = false) String timeStamp)
            throws IOException {

        HashMap<String, Object> map = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        String result = "";
        int currentDocumentID;

        Integer userID = userInfoService.getUserByName(userName).get(0).getIduser();
        String logPath = CommonOperation.getLogPath(filePath);
        Integer fileOwner = userID;
        String newFilePath = CommonOperation.getNewFilePathbyOldFilePath(filePath);

        char sep= LineSeperatorUtil.getLineSeperator();

        currentDocumentID = documentService.getDocumentbyFilePath(
                filePath.replace(sep,'/')).get(0).getIddocument();

        Integer searchFileOwner = cooperateService.getCoopbyDocIDandAuth(
                "own", currentDocumentID).get(0).getUserIduser();

        if (searchFileOwner != fileOwner) {
            fileOwner = searchFileOwner;
        }

        File file = new File(filePath);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        String str = br.readLine();

        while (str != null) {
            sb.append(str.replaceAll("\n", ""))
                    .append(System.getProperty("line.separator"));
            str = br.readLine();
        }
        br.close();
        fr.close();


        File logFile = new File(logPath);
        Map<String, String> versionLog = new HashMap<String, String>();
        Gson gson = new Gson();
        Scanner scanner = new Scanner(logFile);
        String logContent = scanner.useDelimiter("\\Z").next();
        scanner.close();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        versionLog = gson.fromJson(logContent, type);
        Object[] keys = versionLog.keySet().toArray();
        Arrays.sort(keys);

        int node = Arrays.binarySearch(keys, timeStamp);

        str = sb.toString();
        for (int i = keys.length - 1; i >= node; i--) {
            diff_match_patch dmp = new diff_match_patch();
            String strPatches = versionLog.get(keys[i]);
            LinkedList<diff_match_patch.Patch> patches = (LinkedList<diff_match_patch.Patch>) dmp.patch_fromText(strPatches);
            Object[] tmp;
            tmp = dmp.patch_apply(patches, str);
            str = (String) (tmp[0]);
        }

        PrintWriter out = new PrintWriter(filePath);
        out.print(str);
        out.close();

        FileUtils.copyFile(new File(filePath), new File(newFilePath));

        for (int i = keys.length - 1; i >= node; i--) {
            versionLog.remove(keys[i]);
        }

        out = new PrintWriter(logFile);
        out.print(gson.toJson(versionLog));
        out.close();

        map.put("message", "success");

        try {
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * 显示 版本历史信息
     *
     * @param filePath String
     * @param userName String 单击文件保存按钮的操作者
     * @return String
     */
    @RequestMapping(value = "/version-manage/show")
    @ResponseBody
    public String showVersion(@RequestParam(value = "username", required = false) String userName,
                              @RequestParam(value = "filepath", required = false) String filePath) throws IOException {

        HashMap<String, Object> map = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        String result = "";

        Gson gson = new Gson();
        Map<String, String> versionLog = new HashMap<>();
        String[] color;
        Object[] keys;
        Map<String, String> versionItem = new LinkedHashMap<>();


        String logpath = CommonOperation.getLogPath(filePath);

        String sourceString = "{}";    //待写入字符串
        byte[] sourceByte = sourceString.getBytes();

        if (null != sourceByte) {
            File versionLogFile = new File(logpath);
            if (!versionLogFile.exists()) {    //文件不存在则创建文件，先创建目录
                File dir = new File(versionLogFile.getParent());
                dir.mkdirs();
                versionLogFile.createNewFile();
                FileOutputStream outStream = new FileOutputStream(versionLogFile);    //文件输出流用于将数据写入文件
                outStream.write(sourceByte);
                outStream.close();    //关闭文件输出流
            } else {
                Scanner scanner = new Scanner(versionLogFile);
                String content = scanner.useDelimiter("\\Z").next();
                scanner.close();
                Type type = new TypeToken<Map<String, String>>() {
                }.getType();
                versionLog = gson.fromJson(content, type);
            }
        }

        keys = versionLog.keySet().toArray();

        color = new String[1];

        if (versionLog != null) {
            color = new String[versionLog.size()];
            for (int i = 0; i < versionLog.size(); i++) {
                color[i] = ColorUtil.getColor();
            }

        }

        for (int i = 0; i < color.length; i++) {
            versionItem.put((String) keys[i], color[i]);
        }

        map.put("versionItem", versionItem);

        try {
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 显示 编辑历史信息
     *
     * @param filePath  String
     * @param timeStamp String
     * @return String
     */
    @RequestMapping(value = "/version-manage/show-edit")
    @ResponseBody
    public String showEdit(@RequestParam(value = "timestamp", required = false) String timeStamp,
                           @RequestParam(value = "filepath", required = false) String filePath) throws IOException {

        HashMap<String, Object> map = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        String result = "";

        List<Cooperate> coopList;
        HashMap<String, String> user_color = new HashMap<String, String>();

        String logPath = CommonOperation.getLogPath(filePath);
        String editLogPath = CommonOperation.getEditLogByFilePath(filePath);
        File file = new File(filePath);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        Map<String,  String[]> editLog = new HashMap<String,String[]>();
        Map<String, String[]> editLogTmp = new HashMap<String, String[]>();
        String str = br.readLine();

        while (str != null) {
            sb.append(str.replaceAll("\n", ""))
                    .append(System.getProperty("line.separator"));
            str = br.readLine();
        }
        br.close();
        fr.close();

        File logFile = new File(logPath);
        Map<String, String> versionLog = new HashMap<String, String>();
        Gson gson = new Gson();

        Scanner scanner = new Scanner(logFile);
        String logContent = scanner.useDelimiter("\\Z").next();
        scanner.close();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        versionLog = gson.fromJson(logContent, type);
        String[] keys =  versionLog.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        int node = Arrays.binarySearch(keys, timeStamp);

        str = sb.toString();
        for (int i = keys.length - 1; i >= node; i--) {
            diff_match_patch dmp = new diff_match_patch();
            String strPatches = versionLog.get(keys[i]);
            LinkedList<diff_match_patch.Patch> patches = (LinkedList<diff_match_patch.Patch>) dmp.patch_fromText(strPatches);
            Object[] tmp;
            tmp = dmp.patch_apply(patches, str);
            str = (String) (tmp[0]);
        }
        File editLogFile = new File(editLogPath);

        Type editType = new TypeToken<Map<String, String[]>>() {
        }.getType();

        if (editLogFile.exists()){
            scanner = new Scanner(editLogFile);
            String content = scanner.useDelimiter("\\Z").next();
            scanner.close();
            editLog = gson.fromJson(content, editType);
        }
        else{
            String sourceString = "{}";    //待写入字符串
            byte[] sourceByte = sourceString.getBytes();
            File dir = new File(editLogFile.getParent());
            dir.mkdirs();
            editLogFile.createNewFile();
            FileOutputStream outStream = new FileOutputStream(editLogFile);    //文件输出流用于将数据写入文件
            outStream.write(sourceByte);
            outStream.close();    //关闭文件输出流
        }

        String[] editKeys = editLog.keySet().toArray(new String[0]);
        Arrays.sort(editKeys);

        if (node == 0) {
            for (int i = 0; i < editKeys.length; i++) {
                if ((editKeys[i]).compareTo(keys[node]) > 0) {
                    break;
                }
                editLogTmp.put(editKeys[i],editLog.get(editKeys[i]));
            }
        }
        else{
            for (int i = 0; i < editKeys.length; i++) {
                if ((editKeys[i]).compareTo(keys[node]) > 0)
                    break;
                else if((editKeys[i]).compareTo(keys[node-1]) <0 )
                    continue;
                else
                editLogTmp.put(editKeys[i],editLog.get(editKeys[i]));
            }
        }

        map.put("editLog", editLogTmp);
        map.put("content",str);

//        coopList = cooperateService.getCoopByUserIDandAuthWithoutRead(documentService.getDocumentbyFilePath(filePath).get(0).getIddocument());
//
//        for (Cooperate coop: coopList) {
//            user_color.put(userInfoService.getUserByID(coop.getUserIduser()).get(0).getUserName(),ColorUtil.getColor());
//        }
//        map.put("userList",user_color);
        try {
            json = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

}