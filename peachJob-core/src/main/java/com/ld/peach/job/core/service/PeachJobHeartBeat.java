package com.ld.peach.job.core.service;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName PeachJobHeartBeat
 * @Description 心跳类 承载了很多逻辑
 * @Author lidong
 * @Date 2020/10/21
 * @Version 1.0
 */
@Slf4j
public class PeachJobHeartBeat implements Runnable {

    @Override
    public void run() {
        log.info("PeachJobHeartBeat begin");
    }
}
