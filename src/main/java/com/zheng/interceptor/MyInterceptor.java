package com.zheng.interceptor;

import com.zheng.common.BaseContext;
import com.zheng.common.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MyInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if(token==null){
            System.out.println("token为空");
            return true;
        }
        String id = TokenUtil.getId(token);
        BaseContext.setCurrentId(id);
        System.out.println(BaseContext.getCurrentId());
        return true;
    }
}
