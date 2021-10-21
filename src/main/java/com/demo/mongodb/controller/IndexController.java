package com.demo.mongodb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 首页
     */
    @RequestMapping("/")
    public String index() {
        log.info("进入首页");
        return "/index";
    }
}
