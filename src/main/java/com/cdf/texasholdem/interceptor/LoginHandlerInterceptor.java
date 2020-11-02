package com.cdf.texasholdem.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器：进行登录检查
 * 没有登陆的用户不能进 游戏页面
 */
public class LoginHandlerInterceptor implements HandlerInterceptor {
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        Object user = request.getSession().getAttribute("loginUser");
//        if ( null == user ) {
////            request.setAttribute("msg", "没有权限请先登录");
////            request.getRequestDispatcher("/index.html").forward(request, response);
//            return false;
//        } else {
//            return true;
//        }
//    }
//
}
