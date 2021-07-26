package com.upimage.upimg.service;

import com.upimage.upimg.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    //注册
    int userinsert(User user);

    //查询
    List<User> userselect(User user);

    //更改密码
    int updatepass(User user);

    //更新资料
    int updateuserdata(User user);
}
