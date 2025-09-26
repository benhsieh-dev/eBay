package com.ebay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ebay", "controller", "service", "dao", "config"})
@EntityScan(basePackages = {"entity"})
@EnableJpaRepositories(basePackages = {"dao"})
@EnableTransactionManagement
@EnableScheduling
public class EBayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EBayApplication.class, args);
    }
}