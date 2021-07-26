package com.upimage.upimg.service;

import com.upimage.upimg.entity.MailCode;

import java.util.List;

public interface MailCodeService {
    //加入
    int insertmailcode(MailCode mailCode);
    //查询
    List<MailCode> selectmailcode(MailCode mailCode);
    //更新
    int updatemailcode(MailCode mailCode);
}
