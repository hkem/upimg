package com.upimage.upimg.controller;

import com.upimage.upimg.entity.MailCode;
import com.upimage.upimg.entity.User;
import com.upimage.upimg.service.MailCodeService;
import com.upimage.upimg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(produces = "application/json; charset=utf-8")
public class SignupController {

    //返回函数
    RevertMsg msg = new RevertMsg();
    //简单的日期格式化时间
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public UserService userService;

    @Autowired
    public MailCodeService mailCodeService;

    //默认头像
    @Value("${user.user_head}")
    private String user_head;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Resource
    private RedisUtil redisutil;

    @Autowired
    private JavaMailSender mailSender;

    //注册
    @RequestMapping("/signup/register")
    public Map register(HttpServletRequest request){
        String username = request.getParameter("username");
        String user_number = request.getParameter("user_number");
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

        //检查是不是邮件
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(user_number);
        boolean isMatched = matcher.matches();
        if(isMatched == false){
            return  msg.Json_msg(0,"账号错误，请输入正确的账号",new HashMap());
        }

        //查询
        MailCode mailCode_cx = new MailCode();
        mailCode_cx.setUser_number(user_number);
        mailCode_cx.setCode(code);
        List<MailCode> mailCodelist = mailCodeService.selectmailcode(mailCode_cx);
        if(mailCodelist.size() <= 0){
            return  msg.Json_msg(0,"验证码错误",new HashMap());
        }

        //判断账号是否存在
        User user = new User();
        user.setUser_number(user_number);
        List<User> usernumber = userService.userselect(user);
        if(usernumber.size() > 0){
            return msg.Json_msg(0,"账号已存在",new HashMap());
        }

        //写入数据
        user.setUser_head(user_head);
        user.setUser_name(username);
        user.setUser_number(user_number);
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
        String user_head = usernumber.get(0).getUser_head();
        String user_name = usernumber.get(0).getUser_name();
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
        data.put("user_id",user_id);
        data.put("user_head",user_head);
        data.put("user_name",user_name);
        return msg.Json_msg(1,"登录成功",data);
    }


    //发送邮件接口
    @RequestMapping("/signup/setmailcode")
    public Map setmailcode(HttpServletRequest request){
        String user_number = request.getParameter("user_number");
        if(user_number == null){
            return msg.Json_msg(0,"账号不能为空",new HashMap());
        }
        if(user_number == ""){
            return msg.Json_msg(0,"账号不能为空",new HashMap());
        }
        //检查是不是邮件
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(user_number);
        boolean isMatched = matcher.matches();
        if(isMatched == false){
            return  msg.Json_msg(0,"账号错误，请输入正确的账号",new HashMap());
        }

        //随机数
        int max=999999,min=100000;
        int code = (int) (Math.random()*(max-min)+min);

        //发送邮件
        MimeMessage message = mailSender.createMimeMessage();
        String string = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "<div style=\"width: 100%;\"> \n" +
                "  <p>尊敬的客户您好</p>\n" +
                "  <p>您正在注册某某图传网站服务，您的验证码如下：</p>\n" +
                "  <h1 style=\"width: 100%;float: left;text-align: center;padding: 20px 0px 20px 0px;\">"+code+"</h1>\n" +
                "  <p>祝您生活愉快！</p>\n" +
                "</div>\n" +
                "</form>\n" +
                "</body>\n" +
                "<script>\n" +
                "</script>\n" +
                "</html>";

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setFrom("1014095242@qq.com");
            helper.setTo(user_number);
            helper.setSubject("验证码");
            helper.setText(string,true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            return  msg.Json_msg(0,"发送失败",new HashMap());
        }

        //查询
        MailCode mailCode_cx = new MailCode();
        mailCode_cx.setUser_number(user_number);
        List<MailCode> mailCodelist = mailCodeService.selectmailcode(mailCode_cx);
        if(mailCodelist.size() > 0){
            //更新
            MailCode mailCode_up = new MailCode();
            mailCode_up.setCode(code + "");
            mailCode_up.setUser_number(user_number);
            mailCodeService.updatemailcode(mailCode_up);
        }else{
            //添加
            String time = sdf.format(new Date());
            MailCode mailCode = new MailCode();
            mailCode.setUser_number(user_number);
            mailCode.setCode(code + "");
            mailCode.setCreated_at(time);
            mailCode.setUpdated_at(time);
            mailCodeService.insertmailcode(mailCode);
        }
        Map data = new HashMap();
        return msg.Json_msg(1,"发送成功",data);
    }


    //更改密码
    @RequestMapping("/signup/updatepass")
    public Map updatepass(HttpServletRequest request){
        String user_number = request.getParameter("user_number");
        String pass = request.getParameter("pass");
        String code = request.getParameter("code");
        if(pass == ""){
            return msg.Json_msg(0,"密码不能为空",new HashMap());
        }
        if(user_number == null){
            return msg.Json_msg(0,"账号不能为空",new HashMap());
        }
        if(user_number == ""){
            return msg.Json_msg(0,"账号不能为空",new HashMap());
        }
        if(code == ""){
            return msg.Json_msg(0,"验证码不能为空",new HashMap());
        }
        //检查是不是邮件
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(user_number);
        boolean isMatched = matcher.matches();
        if(isMatched == false){
            return  msg.Json_msg(0,"账号错误，请输入正确的账号",new HashMap());
        }
        //判断账号是否存在
        User user_cx = new User();
        user_cx.setUser_number(user_number);
        List<User> usernumber = userService.userselect(user_cx);
        if(usernumber.size() == 0){
            return msg.Json_msg(0,"账号不存在，请前往注册",new HashMap());
        }
        int user_id = usernumber.get(0).getUser_id();
        //判断验证码
        MailCode mailCode_cx = new MailCode();
        mailCode_cx.setUser_number(user_number);
        mailCode_cx.setCode(code);
        List<MailCode> mailCodelist = mailCodeService.selectmailcode(mailCode_cx);
        if(mailCodelist.size() <= 0){
            return msg.Json_msg(0,"验证码错误",new HashMap());
        }
        //加密密码
        User user = new User();
        user.setPass_word(bCryptPasswordEncoder.encode(pass));
        user.setUser_id(user_id);
        //修改密码
        int suee = userService.updatepass(user);
        if(suee > 0){
            return msg.Json_msg(1,"密码重置成功",new HashMap());
        }
        return msg.Json_msg(0,"密码重置失败",new HashMap());
    }
}
