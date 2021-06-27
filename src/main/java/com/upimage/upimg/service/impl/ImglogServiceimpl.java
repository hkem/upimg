package com.upimage.upimg.service.impl;


import com.upimage.upimg.entity.Imglog;
import com.upimage.upimg.entity.User;
import com.upimage.upimg.mapper.ImglogMapper;
import com.upimage.upimg.service.ImglogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ImglogService")
public class ImglogServiceimpl implements ImglogService {

    @Autowired
    private ImglogMapper ImglogMapper;

    //添加
    @Override
    public int imglogadd(Imglog imglog){
        return ImglogMapper.imglogadd(imglog);
    }

    //列表
    @Override
    public List<Imglog> imgloglist(Imglog imglog){
        return ImglogMapper.imgloglist(imglog);
    }

    @Override
    public List<Imglog> imglogcount(Imglog imglog){
        return ImglogMapper.imglogcount(imglog);
    }

    @Override
    public int imglogdelete(Imglog imglog){
        return ImglogMapper.imglogdelete(imglog);
    }

}
