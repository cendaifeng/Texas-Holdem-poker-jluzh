package com.cdf.texasholdem.config;

import com.cdf.texasholdem.interceptor.LoginHandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Springboot 2.0 后摒弃了继承 WebMvcConfigurerAdapter 类
 * 因为 WebMvcConfigurer 接口改用 default 定义方法，无需实现全部方法，也就无需实现空方法的适配器类了
 */
//@Configuration
//public class MvcConfig implements WebMvcConfigurer {
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("login");
//        registry.addViewController("/index.html").setViewName("login");
//    }

    /**
     * 注册拦截器
     * 排除 登录所需的请求路径
     * 排除 静态资源
     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
//                .excludePathPatterns("/", "/index.html", "/user/login")
//                .excludePathPatterns("/webjars/**", "/asserts/**");
//    }
//}
