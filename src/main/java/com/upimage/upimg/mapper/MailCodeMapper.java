package com.upimage.upimg.mapper;


import com.upimage.upimg.entity.MailCode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MailCodeMapper {
    //加入
    int insertmailcode(MailCode mailCode);
    //查询
    List<MailCode> selectmailcode(MailCode mailCode);
    //更新
    int updatemailcode(MailCode mailCode);
}
