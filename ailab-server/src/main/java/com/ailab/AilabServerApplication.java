package com.ailab;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ailab.mapper")
public class AilabServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AilabServerApplication.class, args);
	}

}
