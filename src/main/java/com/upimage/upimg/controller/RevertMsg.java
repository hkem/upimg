package com.upimage.upimg.controller;

import java.util.HashMap;
import java.util.Map;

public class RevertMsg {

    //处理返回信息
    // code= 0,1  msg="成功" data=[]
    public static Map Json_msg(int code,String msg,Map data){
        Map json = new HashMap();
        json.put("code",code);
        json.put("msg",msg);
        json.put("data",data);
        return json;
    }

}
