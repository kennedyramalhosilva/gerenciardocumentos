package com.sistema.gerenciardocumentos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.slf4j.LoggerFactory;

@EnableScheduling
@SpringBootApplication
public class GerenciardocumentosApplication {
	private static final Logger logger = LogManager.getLogger(GerenciardocumentosApplication.class);

	public static void main(String[] args) {
		logger.info("Aplicação iniciada!");
		SpringApplication.run(GerenciardocumentosApplication.class, args);
	}
}
