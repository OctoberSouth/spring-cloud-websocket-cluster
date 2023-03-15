package com.jyn.ws;

import cn.hutool.core.util.IdUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author 10263
 */
@SpringBootApplication
@Import(cn.hutool.extra.spring.SpringUtil.class)
public class WsApplication implements CommandLineRunner {

    public static void main(String[] args) {
        //动态服务名
        System.setProperty("SpringApplicationName", "WS-" + IdUtil.simpleUUID());
        SpringApplication.run(WsApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("项目启动完毕");
    }
}
