package ro.surariu.ioan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication {
	private static final Logger LOG = LogManager.getLogger(MainApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
		LOG.info("Spring Boot application for 'Keycloak API examples' is running");
	}
}
