package com.upimage.upimg.controller;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

@RestController
public class LoginInterceptor extends HandlerInterceptorAdapter {



    private Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);


    @Resource
    private RedisUtil redisutil;

//    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // ①:START 方法注解级拦截器
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 判断接口是否需要登录
        AccessRequired methodAnnotation = method.getAnnotation(AccessRequired.class);
        // 有 @LoginRequired 注解，需要认证
        if (methodAnnotation != null) {
            // 这写你拦截需要干的事儿，比如取缓存，SESSION，权限判断等
            JSONObject res = new JSONObject();
//            //判断token
            String token = request.getHeader("token");
            if(token == null){
                PrintWriter out = response.getWriter();
                res.put("code",-100);
                res.put("msg","您需要登录");
                out.append(res.toString());
                return false;
            }
            //判断token是否存在
            boolean token_redis = redisutil.boolkey(token);
            if(!token_redis){
                PrintWriter out = response.getWriter();
                res.put("code",-100);
                res.put("msg","您需要登录");
                out.append(res.toString());
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }


}
