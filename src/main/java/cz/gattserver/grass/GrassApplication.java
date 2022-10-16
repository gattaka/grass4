package cz.gattserver.grass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"cz.gattserver.grass", "cz.gattserver.web"})
@Import({ BaseConfig.class })
public class GrassApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrassApplication.class, args);
	}

}
