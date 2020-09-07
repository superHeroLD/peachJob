package com.ld.preach.job.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName IndexController
 * @Description Welcome to peachJob
 * @Author lidong
 * @Date 2020/9/7
 * @Version 1.0
 */
@RestController
@RequestMapping("/")
public class IndexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @GetMapping("/")
    public String index() {
        LOGGER.info("Welcome to peachJob");
        return "Welcome to peachJob";
    }

}
