package com.jkys.zyyh.mic.server;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.jkys.zyyh.mic"})
@MapperScan("com.jkys.zyyh.mic.dao")
public class Starter {
	public static void main(String[] args) {
		SpringApplication.run(Starter.class, args);
	}
}
