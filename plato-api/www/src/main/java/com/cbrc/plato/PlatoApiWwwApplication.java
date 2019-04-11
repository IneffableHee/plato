package com.cbrc.plato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PlatoApiWwwApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlatoApiWwwApplication.class, args);
    }

}
