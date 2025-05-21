package org.demo.baoleme;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@MapperScan("org.demo.baoleme.mapper")
@EnableWebSocket
public class BaolemeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaolemeApplication.class, args);
    }

}
