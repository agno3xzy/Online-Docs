package com.OnlineDocs.file;

import java.io.*;
import java.util.ArrayList;

public class FileRow {
    private File file;

    public FileRow(String path){
        this.file=new File(path);
    }

    //按行写文件
    public void writeFile(ArrayList<String> content){
        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (String l:content) {
                bw.write(l);
                if (l != content.get(content.size()-1)){
                    bw.newLine();
                }
            }
            bw.flush();
            bw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //按行读文件
    public ArrayList<String> readFile(){
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader bf = new BufferedReader(fr);
            String str="";
            while ((str = bf.readLine()) != null) {
                arrayList.add(str);
            }
            //arrayList.set(arrayList.size()-1,arrayList.get(arrayList.size()-1).substring(0,
            //        arrayList.get(arrayList.size()-1).length()-2));
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
