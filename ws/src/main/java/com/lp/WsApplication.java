package com.lp;

import cn.hutool.core.util.IdUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 10263
 */
@SpringBootApplication
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
