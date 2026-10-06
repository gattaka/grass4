package cz.gattserver.grass.medic;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MedicRequestHandlerConfig {

	public static final String MEDIC_PATH = "medic-files";

	@Bean
	public ServletRegistrationBean<MedicRequestHandler> medicRequestHandlerRegisterBean(MedicRequestHandler handler) {
		return new ServletRegistrationBean<>(handler, "/" + MEDIC_PATH + "/*");
	}
}