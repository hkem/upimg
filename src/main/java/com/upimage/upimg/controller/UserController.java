package com.upimage.upimg.controller;

import com.upimage.upimg.entity.Imglog;
import com.upimage.upimg.entity.MailCode;
import com.upimage.upimg.entity.User;
import com.upimage.upimg.service.ImglogService;
import com.upimage.upimg.service.MailCodeService;
import com.upimage.upimg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Resource
    private RedisUtil redisutil;

    //返回函数
    RevertMsg msg = new RevertMsg();

    @Autowired
    public MailCodeService mailCodeService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ImglogService ImglogService;

    @Autowired
    private JavaMailSender mailSender;

    //简单的日期格式化时间
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");

    //获取用户信息
    @AccessRequired
    @RequestMapping("/user/userdata")
    public Map userdata(HttpServletRequest request){
        //获取token
        String token = request.getHeader("token");
        //根据token获取uid
        String strid = redisutil.get(token).toString();
        if(strid == null){
            return msg.Json_msg(0,"请登录",new HashMap());
        }
        int uid = Integer.parseInt(strid);
        User user_cx = new User();
        user_cx.setUser_id(uid);
        List<User> usernumber = userService.userselect(user_cx);

        //查询总图片数
        Imglog imglog = new Imglog();
        imglog.setU_id(uid);
        List<Imglog> imglist = ImglogService.imglogcount(imglog);
        int imgcount = imglist.get(0).getListcount();

        Map data = new HashMap();
        data.put("user_id",usernumber.get(0).getUser_id());
        data.put("user_head",usernumber.get(0).getUser_head());
        data.put("user_name",usernumber.get(0).getUser_name());
        data.put("created_at",usernumber.get(0).getCreated_at());
        data.put("imgcount",imgcount);
        return msg.Json_msg(1,"ok",data);
    }

    //更改用户信息
    @AccessRequired
    @RequestMapping("/user/edituserdata")
    public Map edituserdata(HttpServletRequest request){
        String user_head = request.getParameter("user_head");
        String user_name = request.getParameter("user_name");

        if(user_head == null){
            return msg.Json_msg(0,"头像不能为空",new HashMap());
        }
        if(user_head == ""){
            return msg.Json_msg(0,"头像不能为空",new HashMap());
        }
        if(user_name == null){
            return msg.Json_msg(0,"昵称不能为空",new HashMap());
        }
        if(user_name == ""){
            return msg.Json_msg(0,"昵称不能为空",new HashMap());
        }

        //获取token
        String token = request.getHeader("token");
        //根据token获取uid
        String strid = redisutil.get(token).toString();
        if(strid == null){
            return msg.Json_msg(0,"请登录",new HashMap());
        }
        int uid = Integer.parseInt(strid);
        User user = new User();
        user.setUser_id(uid);
        user.setUser_name(user_name);
        user.setUser_head(user_head);
        int suee = userService.updateuserdata(user);
        if(suee > 0){
            return msg.Json_msg(1,"更改成功",new HashMap());
        }else{
            return msg.Json_msg(0,"更改失败",new HashMap());
        }

    }

    //上传头像
    @AccessRequired
    @RequestMapping("/user/useruploadimg")
    public Map useruploadimg(@RequestParam(value = "file",required = false) MultipartFile uploadFile, HttpServletRequest req){


        //获取token
        String token = req.getHeader("token");
        //根据token获取uid
        String strid = redisutil.get(token).toString();
        if(strid == null){
            return msg.Json_msg(0,"请登录",new HashMap());
        }
        int uid = Integer.parseInt(strid);

        String realPath = req.getServletContext().getRealPath("") + "uploaded/user/";//路径
        String format = sdf.format(new Date());//时间
        File folder = new File(realPath + format);//生成路径+时间
        //判断是否存在  不存在创建
        if (!folder.isDirectory()) {
            folder.mkdirs();//创建
        }
        String oldName = uploadFile.getOriginalFilename();//获取上传文件名
        if(oldName.length() > 1024*10){ //判断文件是否超过大小
            return msg.Json_msg(0,"文件太大了",new HashMap());
        }
        String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."), oldName.length());//重新命名
        try {
            // 文件保存
            uploadFile.transferTo(new File(folder, newName));

            String path = "/uploaded/user/" + format + newName;

            // 返回上传文件的访问路径
//            String filePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/upimg/uploaded/user/" + format + newName;
            //构建返回方式
            Map data = new HashMap();
            data.put("img_url",path);
            return msg.Json_msg(1,"ok",data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg.Json_msg(0,"上传失败",new HashMap());
    }

    //更改密码
    @AccessRequired
    @RequestMapping("/user/usereditpass")
    public Map usereditpass(HttpServletRequest request){
        String pass = request.getParameter("pass");
        String code = request.getParameter("code");
        //获取token
        String token = request.getHeader("token");
        //根据token获取uid
        String strid = redisutil.get(token).toString();
        if(strid == null){
            return msg.Json_msg(0,"请登录",new HashMap());
        }
        int uid = Integer.parseInt(strid);

        if(pass == ""){
            return msg.Json_msg(0,"密码不能为空",new HashMap());
        }
        if(code == ""){
            return msg.Json_msg(0,"验证码不能为空",new HashMap());
        }

        //判断账号是否存在
        User user_cx = new User();
        user_cx.setUser_id(uid);
        List<User> usernumber = userService.userselect(user_cx);
        String user_number = usernumber.get(0).getUser_number();
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
        user.setUser_id(uid);
        //修改密码
        int suee = userService.updatepass(user);
        if(suee > 0){
            return msg.Json_msg(1,"密码重置成功",new HashMap());
        }
        return msg.Json_msg(0,"密码重置失败",new HashMap());
    }

    //获取验证码
    @AccessRequired
    @RequestMapping("/user/getusercode")
    public Map getusercode(HttpServletRequest request){

        //获取token
        String token = request.getHeader("token");
        //根据token获取uid
        String strid = redisutil.get(token).toString();
        int uid = Integer.parseInt(strid);

        //随机数
        int max=999999,min=100000;
        int code = (int) (Math.random()*(max-min)+min);
        //判断账号是否存在
        User user_cx = new User();
        user_cx.setUser_id(uid);
        List<User> usernumber = userService.userselect(user_cx);
        String user_number = usernumber.get(0).getUser_number();

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

}
