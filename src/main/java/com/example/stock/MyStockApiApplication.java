package com.example.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyStockApiApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(MyStockApiApplication.class);

	public static void main(String[] args) {
		logger.info("############## MyStockApiApplication Start ############");
		SpringApplication.run(MyStockApiApplication.class, args);
	}

}
