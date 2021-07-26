package com.upimage.upimg.service;

import com.upimage.upimg.entity.Imglog;

import java.util.List;


public interface ImglogService {

    //添加
    int imglogadd(Imglog imglog);

    //获取列表
    List<Imglog> imgloglist(Imglog imglog);

    //获取全部条数
    List<Imglog> imglogcount(Imglog imglog);

    //删除
    int imglogdelete(Imglog imglog);

    //浏览加1
    int browseimage(Imglog imglog);
}
