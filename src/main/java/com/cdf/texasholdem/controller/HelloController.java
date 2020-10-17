package com.cdf.texasholdem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        logger.trace("===test logger work===");
        return "Hello Springboot";
    }

}
