package com.upimage.upimg.controller;

import com.upimage.upimg.entity.Imglog;
import com.upimage.upimg.service.ImglogService;
import oracle.jrockit.jfr.VMJFR;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
public class UploadController {
    //简单的日期格式化时间
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
    //返回函数
    RevertMsg msg = new RevertMsg();




    @Resource
    private RedisUtil redisutil;

    @Autowired
    private ImglogService ImglogService;

    //上传图片文件
    @PostMapping("/upload/uploadimg")
    public Map uploadimg(@RequestParam(value = "file",required = false) MultipartFile uploadFile, HttpServletRequest req){
        String realPath = req.getServletContext().getRealPath("") + "uploaded/";//路径
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

            String path = "/uploaded/" + format + newName;

            // 返回上传文件的访问路径
            String filePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/upimg/uploaded/" + format + newName;
            //构建返回方式
            Map data = new HashMap();
            data.put("img_url",filePath);

            //判断有没有token 有的话调用保存接口
            String token = req.getHeader("token");
            if(token != null){

                String struserid = redisutil.get(token).toString();
                if(struserid != null){
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    String time = sdf2.format(new Date());
                    int user_id = Integer.parseInt(struserid);
                    Imglog img = new Imglog();
                    img.setU_id(user_id);
                    img.setImg_path(path);
                    img.setCreated_at(time);
                    img.setUpdated_at(time);
                    ImglogService.imglogadd(img);
                }
            }

            return msg.Json_msg(1,"ok",data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg.Json_msg(0,"上传失败",new HashMap());
    }
}
