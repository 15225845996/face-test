package com.face.facetest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = "com.face")
@MapperScan("com/face/facetest/mapper")
public class FaceTestApplication{

	public static void main(String[] args) {
		SpringApplication.run(FaceTestApplication.class, args);
	}

}
