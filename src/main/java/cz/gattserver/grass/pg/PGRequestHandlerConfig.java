package cz.gattserver.grass.pg;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PGRequestHandlerConfig {

    public static final String PG_PATH = "pg-files";

    @Bean
    public ServletRegistrationBean<PGRequestHandler> pgRequestHandlerRegisterBean(PGRequestHandler handler) {
        return new ServletRegistrationBean<>(handler, "/" + PG_PATH + "/*");
    }
}