package com.OnlineDocs.service.Impl;

import com.OnlineDocs.dao.InviteInfoMapper;
import com.OnlineDocs.entity.Document;
import com.OnlineDocs.entity.InviteInfo;
import com.OnlineDocs.service.InviteInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

public class InviteInfoServiceImpl implements InviteInfoService {

    @Autowired
    InviteInfoMapper inviteInfoMapper;

    @Override
    public List<InviteInfo> getLatestInviteByDocID(int docID)
    {
        Example example = new Example(InviteInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("documentId",docID);
        example.setOrderByClause("inviteTime");
        return inviteInfoMapper.selectByExample(example);
    }

    @Override
    public List<InviteInfo> getInviteByDocID(int docID)
    {
        Example example = new Example(InviteInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("documentId",docID);
        return inviteInfoMapper.selectByExample(example);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public String deleteInviteByInviteID(int inviteID)
    {

        Example example = new Example(InviteInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("inviteId",inviteID);
        try {
            inviteInfoMapper.deleteByExample(example);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "false";
        }
        return "success";
    }

    @Override
    public List<InviteInfo> getInviteByDocIDUserIDAuth(int docID,int userID,String authority)
    {
        Example example = new Example(InviteInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("documentId",docID);
        criteria.andEqualTo("inviteUserId",userID);
        criteria.andEqualTo("auth",authority);

        return inviteInfoMapper.selectByExample(example);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public String insertInviteInfo(int docID,String auth,Date inviteTime,int userID)
    {
        try{
            inviteInfoMapper.insertInviteInfo(docID,userID,auth,inviteTime);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return "fail";
        }

        return "success";


    }
}
