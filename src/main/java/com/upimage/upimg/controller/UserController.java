package com.upimage.upimg.controller;

import com.upimage.upimg.entity.User;
import com.upimage.upimg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Resource
    private RedisUtil redisutil;

    //注册
    @AccessRequired
    @RequestMapping("/user/userinsert")
    public String userinsert(){
//        //加密
//        String jiami = bCryptPasswordEncoder.encode("9999");
//        //解密
//        boolean awdwad = bCryptPasswordEncoder.matches("9999","$2a$10$7AAXawPAEudDVTaLAJZtOOVuOogZxkg7SkG4xKUOEr1WM2x8H8NmO");
//        Map data = new HashMap();
//        data.put("name","");
//        data.put("age","age");
//        boolean redisutil2 =redisutil.boolkey("$2a$10$AujwXRJJA.8n/IyuBA5HMOytK32jwG4QfxE2NN3Mf4.HD01dYzvjq");
        Object awd = redisutil.get("$2a$10$AujwXRJJA.8n/IyuBA5HMOytK32jwG4QfxE2NN3Mf4.HD01dYzvjq");

        return awd.toString();
    }

}
