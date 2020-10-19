package com.ld.peach.job.admin.controller;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping("/")
public class IndexController {

    @GetMapping("/")
    public String index() {
        log.info("Welcome to peachJob");
        return "Welcome to peachJob";
    }

}
