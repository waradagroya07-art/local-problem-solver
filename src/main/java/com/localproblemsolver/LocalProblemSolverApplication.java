package com.localproblemsolver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LocalProblemSolverApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalProblemSolverApplication.class, args);
	}
}