package com.mine.ide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class IdeApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(IdeApplication.class, args);
    }
    @Override//为了打包Spring boot项目
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }
}
