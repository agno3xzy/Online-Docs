package com.OnlineDocs.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CommonOperation {


    public static String getEditLogByFilePath(String filePath) {
        char sep=LineSeperatorUtil.getLineSeperator();
        String[] path;
        if (sep=='\\'){
            path = filePath.split("\\\\");
        }
        else{
            path = filePath.split("/");
        }
        String fileName = path[path.length - 1].split("\\.")[0];
        fileName = fileName + "_edit.json";
        path[path.length - 1] = fileName;
        path[path.length - 2] = "log";
        return StringUtils.join(path, sep);
    }

    public static String getNewFilePathbyOldFilePath(String oldfilepath) {
        char sep=LineSeperatorUtil.getLineSeperator();
        String[] path;
        if (sep=='\\'){
            path = oldfilepath.split("\\\\");
        }
        else{
            path = oldfilepath.split("/");
        }
        String fileName = path[path.length - 1].split("\\.")[0];
        fileName = fileName + "_t.txt";
        path[path.length - 1] = fileName;
        return StringUtils.join(path, sep);

    }

    public static String getTimeStamp() {
        long time = System.currentTimeMillis();
        String t = String.valueOf(time / 1000);
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(t + "000")));
    }


    public static String getLogPath(String filePath) {
        char sep=LineSeperatorUtil.getLineSeperator();
        String[] path;
        if (sep=='\\'){
            path = filePath.split("\\\\");
        }
        else{
            path = filePath.split("/");
        }
        path[path.length - 2] = "log";
        path[path.length - 1] = path[path.length - 1].replace( ".txt","_version.json");
        return StringUtils.join(path, sep);
    }

    public static String getFilename(String filePath) {
        char sep=LineSeperatorUtil.getLineSeperator();
        String[] path;
        if (sep=='\\'){
            path = filePath.split("\\\\");
        }
        else{
            path = filePath.split("/");
        }
        return path[path.length - 1];
    }

}
