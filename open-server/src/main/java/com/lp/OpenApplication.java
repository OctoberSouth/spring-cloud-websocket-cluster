package com.lp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author lp
 */
@SpringBootApplication
@EnableFeignClients
public class OpenApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(OpenApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("项目启动完毕");
    }

}
