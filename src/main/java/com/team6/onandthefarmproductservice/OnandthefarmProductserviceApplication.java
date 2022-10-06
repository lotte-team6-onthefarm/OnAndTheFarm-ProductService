package com.team6.onandthefarmproductservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OnandthefarmProductserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnandthefarmProductserviceApplication.class, args);
	}

}
