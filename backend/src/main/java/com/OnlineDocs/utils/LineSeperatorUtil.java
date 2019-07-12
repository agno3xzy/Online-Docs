package com.OnlineDocs.utils;

import java.io.*;

public class LineSeperatorUtil {

    public static char getLineSeperator() {
        char str = File.separatorChar;
        return str;
    }

    public static String setPathbyOS(String oldPath) {
        if (getLineSeperator()=='/' && oldPath.contains("\\\\")) {
            return oldPath.replaceAll("\\\\", "/");
        }
        else if(getLineSeperator()=='\\' && oldPath.contains("/")){
            return oldPath.replaceAll("/", "\\\\");
        }
        return oldPath;
    }

}
