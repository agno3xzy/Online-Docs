package com.OnlineDocs.file;

import com.OnlineDocs.controller.ConflictController;
import com.OnlineDocs.file.FileList;
import com.OnlineDocs.file.FileOperation;
import com.OnlineDocs.utils.CommonOperation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.*;

public class SaveEditVersion {

    /**
     * 添加 用户原子操作
     *
     * @param optTable String[]
     * @param filepath String
     */
    public static void addEdit(String[] optTable, String filepath)
            throws IOException {

        String editLogPath = CommonOperation.getEditLogByFilePath(filepath);

        Map<String, String[]> editLog = new HashMap<String, String[]>();

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        File editLogFile = new File(editLogPath);

        Type type = new TypeToken<Map<String, String[]>>() {
        }.getType();

        if (editLogFile.exists() && editLogFile.length() != 0) {
            Scanner scanner = new Scanner(editLogFile);
            String content = scanner.useDelimiter("\\Z").next();
            scanner.close();
            editLog = gson.fromJson(content, type);
//            Object[] keys;
//            Set tmp;
//            tmp = editLog.keySet();
//            keys = tmp.toArray(new String[0]);
            String[] keys = editLog.keySet().toArray(new String[0]);
            Arrays.sort(keys);

            if (Arrays.asList(keys).contains(optTable[optTable.length - 1])) {
                List list = new ArrayList(Arrays.asList(Arrays.copyOfRange(optTable, 0, optTable.length - 1)));
                list.addAll(Arrays.asList(editLog.get(optTable[optTable.length - 1])));
                String[] result = (String[]) list.toArray(new String[0]);
                editLog.put(optTable[optTable.length - 1], result);
            } else {
                editLog.put(optTable[optTable.length - 1], Arrays.copyOfRange(optTable, 0, optTable.length - 1));
            }
        } else {
            File dir = new File(editLogFile.getParent());
            dir.mkdirs();
            editLogFile.createNewFile();
            editLog.put(optTable[optTable.length - 1], Arrays.copyOfRange(optTable, 0, optTable.length - 1));
        }
        FileOutputStream fileOutputStream = new FileOutputStream(editLogFile);
        PrintStream ps = new PrintStream(fileOutputStream);
        ps.print(gson.toJson(editLog, type));
        ps.close();
        fileOutputStream.close();
    }

}