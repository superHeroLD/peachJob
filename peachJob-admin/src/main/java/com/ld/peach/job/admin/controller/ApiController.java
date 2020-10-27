package com.ld.peach.job.admin.controller;

import com.ld.peach.job.core.constant.TaskConstant;
import com.ld.peach.job.core.starter.TaskScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName ApiController
 * @Description TODO
 * @Author lidong
 * @Date 2020/10/17
 * @Version 1.0
 */
@Slf4j
@Controller
public class ApiController {

    @RequestMapping(TaskConstant.REGISTER_API)
    public void api(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TaskScheduler.invokeAdminService(request, response);
    }
}
