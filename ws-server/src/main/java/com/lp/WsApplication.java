package com.lp;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author lp
 */
@SpringBootApplication
@EnableScheduling
@Import(SpringUtil.class)
@EnableFeignClients
public class WsApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WsApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("项目启动完毕");
    }

}
