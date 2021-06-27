package com.upimage.upimg.mapper;

import com.upimage.upimg.entity.Imglog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImglogMapper {

    //添加
    int imglogadd(Imglog imglog);

    //列表
    List<Imglog> imgloglist(Imglog imglog);

    //获取全部条数
    List<Imglog> imglogcount(Imglog imglog);

    //删除
    int imglogdelete(Imglog imglog);
}
