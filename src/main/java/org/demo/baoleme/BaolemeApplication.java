package org.demo.baoleme;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("org.demo.baoleme.mapper")
public class BaolemeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaolemeApplication.class, args);
    }

}
