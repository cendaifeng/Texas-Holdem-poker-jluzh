package com.cdf.texasholdem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {

    @PostMapping("/user/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        Map<String, Object> map, HttpSession session) {
        if ("admin".equals(username) && "123".equals(password)) {
            session.setAttribute("loginUser", username);
            return "redirect:/main.html";
        } else {
            map.put("msg", "账号或密码错误");
            return "login";
        }
    }

    // 在密码错误后，刷新页面的 get 请求映射
    @RequestMapping("/user/login")
    public String login() {
        return "login";
    }
}
