package com.cdf.texasholdem.controller;

import com.cdf.texasholdem.bean.Person;
import com.cdf.texasholdem.mapper.PersonMapper;
import com.cdf.texasholdem.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AccessController {

    @Autowired
    PersonMapper personMapper;

    @Autowired
    PersonService personService;

    @PostMapping("/user/login")
    public String login(@RequestParam String id, @RequestParam String password,
                        Map<String, Object> map, HttpServletResponse response) {
        Person personById = personMapper.getPersonById(id);
        String passwordFormP = personById.getPassword();
        String name = personById.getName();

        if (password.equals(passwordFormP)) {
            Cookie cookie = new Cookie("loginUser",name);
            cookie.setMaxAge(86400);
            cookie.setPath("/");
            response.addCookie(cookie);
            return "redirect:/rungame/list";
//            return "hall";
        } else {
            map.put("msg", "账号或密码错误");
            return "login";
        }
    }

    @PostMapping("/user/register")
    public String insertPerson(@Valid Person person, BindingResult result,
//                            @RequestParam String id, @RequestParam String password, @RequestParam String name, @RequestParam Integer bankroll,
                            @RequestParam String repassword,
                            Map<String, Object> map) {
        // 若校验失败，会显示错误信息
        if (result.hasErrors()) {
            HashMap<String, Object> errorMap = new HashMap<>();
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError fieldError : errors) {
                System.out.println("错误字段："+fieldError.getField());
                System.out.println("错误信息："+fieldError.getDefaultMessage());
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            map.put("msg", errorMap);
            return "register";
        }
        if (!personService.checkUserid(person.getId())) {
            map.put("msg", "用户名已被注册");
            return "register";
        }
        if (person.getPassword().equals(repassword)) {
            personMapper.insertPerson(person);
            return "login";
        } else {
            map.put("msg", "密码不一致");
            return "register";
        }

    }

    // 在密码错误后，刷新页面的 get 请求映射
    @RequestMapping("/user/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/user/register")
    public String register() {
        return "register";
    }
}
