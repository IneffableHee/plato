package com.cbrc.plato;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@MapperScan("com.cbrc.plato.core.*.dao")
public class PlatoApiAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatoApiAdminApplication.class, args);
    }

}
