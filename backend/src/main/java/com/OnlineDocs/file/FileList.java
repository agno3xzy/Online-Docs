package com.OnlineDocs.file;

import javax.servlet.ServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileList {
    private List<FileOperation> FopList;

    public FileList(){
        this.FopList=new ArrayList<FileOperation>();
    }

    //检查这个文档的修改记录是否已被追踪
    private int isContains(String path_t){
        int temp=-1;
        for (FileOperation f:this.FopList){
            if (f.getNewFilePath().equals(path_t)){
                temp=this.FopList.indexOf(f);
                break;
            }
        }
        return temp;
    }

    //删除这个文档的修改记录
    public boolean deleteFop(String path_t){
        boolean result=false;
        for (FileOperation f:this.FopList){
            if (f.getNewFilePath().equals(path_t)){
                this.FopList.remove(f);
                result=true;
                break;
            }
        }
        return result;
    }

    //根据文档路径获取相应文档操作类
    public FileOperation getFop(String newpath){
        FileOperation fop_t;
        int int_temp=isContains(newpath);
        if (int_temp>=0){
            fop_t=this.FopList.get(int_temp);
        }
        else{
            return null;
        }
        return fop_t;
    }


    //创建文档操作类
    public FileOperation createFop(String newpath,String oldpath){
        FileOperation fop_t;
        fop_t=new FileOperation(oldpath,newpath,0,new HashMap<String, Integer>(),new ArrayList<String[]>());
        this.FopList.add(fop_t);
        return fop_t;
    }
}
