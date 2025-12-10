package com.hospital_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HospitalBackendUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalBackendUserApplication.class, args);

		System.out.println("Application is Running..........!");
	}

}
