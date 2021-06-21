package com.upimage.upimg.controller;

import com.upimage.upimg.entity.User;
import com.upimage.upimg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    //简单的日期格式化时间
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");

    @RequestMapping("/findAll")
    public List<User> findAll(){
        return userService.findAll();
    }



    //注册
    @RequestMapping("/user/userinsert")
    public String userinsert(){
        User user = new User();
        user.setUser_id(2);
        user.setUser_head("www.women");
        user.setUser_name("第二个");
        user.setAge(99);
        user.setSex("男");
        String time = sdf.format(new Date());
        user.setCreated_at(time);
        user.setUpdated_at(time);
        user.setPass_word("9999");
        userService.userinsert(user);
        return "ok";
    }

}
