package com.upimage.upimg.controller;

import com.upimage.upimg.entity.Imglog;
import com.upimage.upimg.service.ImglogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(produces = "application/json; charset=utf-8")
public class ImglogController {

    @Autowired
    private ImglogService ImglogService;

    //返回函数
    RevertMsg msg = new RevertMsg();

    @Resource
    private RedisUtil redisutil;

    //获取列表
    @AccessRequired
    @RequestMapping("/imglog/imglist")
    public Map imglist(HttpServletRequest request){
        //获取token
        String token = request.getHeader("token");
        String pagestr = request.getParameter("page");

        int Pagecount = 5;//每页条数
        int pageint = pagestr == null ? 1 : Integer.parseInt(pagestr);
        int page = (pageint * Pagecount) - Pagecount;
        //根据token获取uid
        String strid = redisutil.get(token).toString();
        if(strid == null){
            return msg.Json_msg(0,"请登录",new HashMap());
        }
        int uid = Integer.parseInt(strid);
        //拼接查询数据
        Imglog imglog = new Imglog();
        imglog.setU_id(uid);

        //获取全部条数
        List<Imglog> countlist = ImglogService.imglogcount(imglog);
        int page_count = countlist.get(0).getListcount();

        imglog.setPage(page);
        imglog.setPagecount(Pagecount);
        List<Imglog> imglist = ImglogService.imgloglist(imglog);
        //组装返回数据
        Map data = new HashMap();
        data.put("list",imglist);
        data.put("current_page",pageint);
        data.put("next_page",pageint + 1);
        data.put("page_count",page_count);
        return msg.Json_msg(1,"ok",data);
    }




    //删除
    @AccessRequired
    @RequestMapping("/imglog/deleteimglog")
    public Map deleteimglog(HttpServletRequest request){
        //获取token
        String token = request.getHeader("token");
        String strlogid = request.getParameter("id");
        if(strlogid == null){
            return msg.Json_msg(0,"请选择删除图片",new HashMap());
        }
        int logid = Integer.parseInt(strlogid);

        //根据token获取uid
        String strid = redisutil.get(token).toString();
        if(strid == null){
            return msg.Json_msg(0,"请登录",new HashMap());
        }
        int uid = Integer.parseInt(strid);
        Imglog imglog = new Imglog();
        imglog.setU_id(uid);
        imglog.setImglog_id(logid);
        try {
            int delete = ImglogService.imglogdelete(imglog);
            if(delete == 1){
                return msg.Json_msg(1, "删除成功", new HashMap());
            }else{
                return msg.Json_msg(0,"删除失败",new HashMap());
            }

        }catch (Exception e){

        }
        return msg.Json_msg(0,"删除失败",new HashMap());
    }



}
