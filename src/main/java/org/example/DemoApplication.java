package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @Author sunmengjin
 * @Create DemoApplication.java 2024/1/5 15:24
 * @Description:
 */
@Slf4j
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DemoApplication.class);
        ConfigurableApplicationContext run = application.run(args);
    }
}
