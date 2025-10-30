package com.ceos22.cgvclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class CgvcloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(CgvcloneApplication.class, args);
	}

}
