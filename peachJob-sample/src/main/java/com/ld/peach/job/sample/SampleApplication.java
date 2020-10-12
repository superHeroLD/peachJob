package com.ld.peach.job.sample;

import com.ld.peach.job.core.starter.EnablePeachJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName SampleApplication
 * @Description TODO
 * @Author lidong
 * @Date 2020/10/12
 * @Version 1.0
 */
@EnablePeachJob
@SpringBootApplication
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

}
