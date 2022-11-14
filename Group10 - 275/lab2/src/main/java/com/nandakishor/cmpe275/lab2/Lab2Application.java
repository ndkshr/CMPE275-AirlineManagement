package com.nandakishor.cmpe275.lab2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

//@ComponentScan
//@EnableAutoConfiguration
@SpringBootApplication
public class Lab2Application {

	public static void main(String[] args) {
		SpringApplication.run(Lab2Application.class, args);
	}
}
