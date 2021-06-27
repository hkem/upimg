package com.upimage.upimg.mapper;

import com.upimage.upimg.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> findAll();

    //注册
    int userinsert(User user);

    //查询
    List <User> userselect(User user);


    //登录



}
