package com.upimage.upimg.controller;

import com.upimage.upimg.entity.User;
import com.upimage.upimg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(produces = "application/json; charset=utf-8")
public class SignupController {

    //返回函数
    RevertMsg msg = new RevertMsg();
    //简单的日期格式化时间
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public UserService userService;

    //默认头像
    @Value("${user.user_head}")
    private String user_head;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private RedisUtil redisutil;


    //注册
    @RequestMapping("/signup/register")
    public Map register(HttpServletRequest request){
        String username = request.getParameter("username");
        String pass = request.getParameter("pass");
        String code = request.getParameter("code");
        if(username == ""){
            return msg.Json_msg(0,"用户名不能为空",new HashMap());
        }

        if(pass == ""){
            return msg.Json_msg(0,"密码不能为空",new HashMap());
        }

        if(code == ""){
            return msg.Json_msg(0,"验证码不能为空",new HashMap());
        }

        //判断账号是否存在
        User user = new User();
        user.setUser_number(username);
        List<User> usernumber = userService.userselect(user);
        if(usernumber.size() > 0){
            return msg.Json_msg(0,"账号已存在",new HashMap());
        }

        //写入数据
        user.setUser_head(user_head);
        user.setUser_name(username);
        user.setUser_number(username);
        user.setAge(18);
        user.setSex("男");
        String time = sdf.format(new Date());
        user.setCreated_at(time);
        user.setUpdated_at(time);
        user.setPass_word(bCryptPasswordEncoder.encode(pass));

        try {
            int suee = userService.userinsert(user);
            if(suee == 1){
                return msg.Json_msg(1,"注册成功",new HashMap());
            }else{
                return msg.Json_msg(0,"注册失败",new HashMap());
            }
        }catch (Exception e){

        }
        return msg.Json_msg(0,"注册失败",new HashMap());

    }

    //登录
    @RequestMapping("/signup/signin")
    public Map signin(HttpServletRequest request){
        String username = request.getParameter("username");
        String pass = request.getParameter("pass");
        if(username == ""){
            return msg.Json_msg(0,"用户名不能为空",new HashMap());
        }

        if(pass == ""){
            return msg.Json_msg(0,"密码不能为空",new HashMap());
        }

        //查询
        User user = new User();
        user.setUser_number(username);
        List<User> usernumber = userService.userselect(user);
        if(usernumber.size() == 0){
            return msg.Json_msg(0,"账号不存在，请前往注册",new HashMap());
        }
        String encryptionpass = usernumber.get(0).getPass_word();
        int user_id = usernumber.get(0).getUser_id();
        if(!bCryptPasswordEncoder.matches(pass,encryptionpass)){
            return msg.Json_msg(0,"密码错误",new HashMap());
        }

        String strid = String.valueOf(user_id);
        String token = "";
        try {
            //密码正确 生成token存入redis
            //生成token
            token = bCryptPasswordEncoder.encode(strid + UUID.randomUUID().toString());
            //存入redis
            redisutil.set(token,strid,60*60*24*100);
        }catch (Exception e){
            return msg.Json_msg(0,"登录失败",new HashMap());
        }
        Map data = new HashMap();
        data.put("token",token);
        return msg.Json_msg(1,"登录成功",data);
    }






}
