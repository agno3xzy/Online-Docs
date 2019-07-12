package com.OnlineDocs.service;

import com.OnlineDocs.file.FileList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import com.OnlineDocs.file.FileListInstance;

@Service
public class Initialization implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception{
        FileListInstance.FopList=new FileList();
    }

}
