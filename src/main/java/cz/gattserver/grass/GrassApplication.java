package cz.gattserver.grass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ BaseConfig.class })
public class GrassApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrassApplication.class, args);
	}

}
