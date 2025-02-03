package concert.interfaces;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = { "concert.interfaces", "concert.infrastructure", "concert.domain", "concert.commons", "concert.application"})
public class ConcertApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConcertApplication.class, args);
	}
}
