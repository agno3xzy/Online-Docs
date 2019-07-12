package com.OnlineDocs.file;

import com.OnlineDocs.WebSocket.SocketHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileOperation {
    private String oldFilePath;
    private String newFilePath;
    private int index_log;//这一次记录日志应传递的操作序列开始位置
    private Map<String,Integer> user_index;//这一次更新文档应传递的操作序列开始位置
    private File newFile;
    private File oldFile;
    private List<String[]> optable;//操作序列表格（插入/删除，行数(从零开始)，开始位置(从零开始)，内容，是否插入新行/删除旧行，用户名（非ID）,时间戳）

    public FileOperation(String oldFilePath,String newFilePath,int index_log,Map<String,Integer> user_index,List<String[]> optable){
        this.index_log=index_log;
        this.user_index=user_index;
        this.newFilePath=newFilePath;
        this.oldFilePath=oldFilePath;
        this.optable=optable;
        this.oldFile=new File(oldFilePath);
        this.newFile=new File(newFilePath);
//        try{
//            this.newFile.createNewFile();
//            copyFile(this.oldFile,this.newFile);
//        }
//        catch (Exception e){
//
//        }
    }

    //记录日志时获取上一次记录日志后的所有操作序列
    public List<String[]> logFile(){
        List<String[]> temp=new ArrayList<>();
        try{
            int i=this.index_log;
            while (i<this.optable.size()){
                temp.add(this.optable.get(i));
                i++;
            }
            this.index_log=this.optable.size();
            copyFile(this.newFile,this.oldFile);
        }
        catch (Exception e){

        }
        return temp;
    }

    public String getOldFilePath(){
        return oldFilePath;
    }

    public void setOldFilePath(String oldFilePath){
        this.oldFilePath=oldFilePath;
    }

    public String getNewFilePath(){
        return newFilePath;
    }

    public void setNewFilePath(String newFilePath){
        this.newFilePath=newFilePath;
    }

    public File getOldFile(){
        return oldFile;
    }

    public File getNewFile(){
        return newFile;
    }

    public int getIndex_log(){
        return index_log;
    }

    public void setIndex_log(int index_log){
        this.index_log=index_log;
    }

    public int getUser_index(String username){
        return user_index.get(username);
    }

    public int setUser_index(String username,int index){
        return user_index.put(username,index);
    }

    //检测某用户更改痕迹是否已被记录，若无则新增记录
    public boolean insertUser_index(String username){
        if (!this.user_index.containsKey(username)){
            this.user_index.put(username,0);
            return false;
        }
        return true;
    }

    public List<String[]> getOptable(){
        return optable;
    }

    //删除上一次用户的操作
    public String[] deleteLastUserRecord(String username){
        int index = this.getUser_index(username);
        index--;
        String[] opList=this.optable.get(index);
        this.optable.remove(index);
        return opList;
    }

    public void addOptable(String[] s){
        this.optable.add(s);
    }

    // 复制文件
    private void copyFile(File sourceFile, File targetFile)
            throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff=new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff=new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len =inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    //根据历史数据修正行数和行内开始位置
    public String[] merge(String[] op){
        Logger.getLogger(FileOperation.class).info(op[0]+'\n'+op[1]+'\n'+
                op[2]+'\n'+op[3]+'\n'+op[4]+'\n'+op[5]);
        int i=this.getUser_index(op[5]);
        int line=Integer.valueOf(op[1]);
        int start=Integer.valueOf(op[2]);
        boolean line_change;
        if (op[4].equals("true")){
            line_change=true;
        }
        else{
            line_change=false;
        }
        if (line_change){
            while (i<this.optable.size()){
                while ((i<this.optable.size()) &&
                        !(Integer.valueOf(this.optable.get(i)[1])<=line
                        && this.optable.get(i)[4].equals("true"))){
                    i++;
                }
                if (i==this.optable.size()){
                    break;
                }
                if (this.optable.get(i)[0].equals("ins")){
                    line++;
                }
                else if (this.optable.get(i)[0].equals("del")){
                    line--;
                }
            }
        }
        else{
            while (i<this.optable.size()){
                while ((i<this.optable.size()) &&
                        ((Integer.valueOf(this.optable.get(i)[1])<line
                        && !this.optable.get(i)[4].equals("true"))
                        ||
                        (Integer.valueOf(this.optable.get(i)[1])==line
                                && (Integer.valueOf(this.optable.get(i)[2])>start
                                || this.optable.get(i)[4].equals("true")))
                        ||
                        (Integer.valueOf(this.optable.get(i)[1])>line))){
                    i++;
                }
                if (i==this.optable.size()){
                    break;
                }
                if (Integer.valueOf(this.optable.get(i)[1])<line && this.optable.get(i)[4].equals("true")) {
                    if (this.optable.get(i)[0].equals("ins")){
                        line++;
                    }
                    else if (this.optable.get(i)[0].equals("del")){
                        line--;
                    }
                }
                else if (Integer.valueOf(this.optable.get(i)[1])==line && Integer.valueOf(this.optable.get(i)[2])<=start){
                    start+=this.optable.get(i)[3].length();
                }
            }
        }
        op[1]=String.valueOf(line);
        op[2]=String.valueOf(start);
        return op;
    }

    //根据操作更新内容
    public ArrayList<String> update_doc(String[] op,ArrayList<String> arrayList){
        int line=Integer.valueOf(op[1]);
        int start=Integer.valueOf(op[2]);
        if (op[4].equals("true")){//整行操作
            if (op[0].equals("ins")){
                arrayList.add(line+1,op[3]);
            }
            else {
                arrayList.remove(line);
            }
        }
        else{//行内操作
            StringBuffer buffer =new StringBuffer(arrayList.get(line));
            if (op[0].equals("ins")){
                buffer.insert(start,op[3]);
                arrayList.set(line,buffer.substring(0));
            }
            else {
                buffer.delete(start,start+op[3].length());
                arrayList.set(line,buffer.substring(0));
            }
        }
        return arrayList;
    }


}
