package com.upimage.upimg.service.impl;

import com.upimage.upimg.entity.MailCode;
import com.upimage.upimg.mapper.MailCodeMapper;
import com.upimage.upimg.service.MailCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("MailCodeService")
public class MailCodeServiceimpl implements MailCodeService {

    @Autowired
    private MailCodeMapper mailCodeMapper;

    @Override
    public int insertmailcode(MailCode mailCode) {
        return mailCodeMapper.insertmailcode(mailCode);
    }

    @Override
    public List<MailCode> selectmailcode(MailCode mailCode) {
        return mailCodeMapper.selectmailcode(mailCode);
    }

    @Override
    public int updatemailcode(MailCode mailCode) {
        return mailCodeMapper.updatemailcode(mailCode);
    }
}
