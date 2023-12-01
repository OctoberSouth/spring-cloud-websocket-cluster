package com.lp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author lp
 */
@SpringBootApplication
@EnableFeignClients
@MapperScan("com.lp.mapper")
public class ActivityApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ActivityApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("项目启动完毕");
    }

}
