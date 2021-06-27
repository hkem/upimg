package com.upimage.upimg.service.impl;

import com.upimage.upimg.entity.User;
import com.upimage.upimg.mapper.UserMapper;
import com.upimage.upimg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("UserService")
public class UserServiceimpl implements UserService {

    @Autowired
    private UserMapper UserMapper;

    @Override
    public List<User> findAll() {
        return UserMapper.findAll();
    }

    //注册
    @Override
    public int userinsert(User user){
        return UserMapper.userinsert(user);
    }

    //查询
    @Override
    public List<User> userselect(User user){
        return UserMapper.userselect(user);
    }
}
