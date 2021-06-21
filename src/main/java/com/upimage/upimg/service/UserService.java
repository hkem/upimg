package com.upimage.upimg.service;

import com.upimage.upimg.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    //注册
    int userinsert(User user);

}
