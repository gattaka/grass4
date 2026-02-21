package cz.gattserver.grass.pg;

import cz.gattserver.grass.pg.config.PGConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PGRequestHandlerConfig {

    @Bean
    public ServletRegistrationBean<PGRequestHandler> pgRequestHandlerRegisterBean(PGRequestHandler handler) {
        return new ServletRegistrationBean<>(handler, "/" + PGConfiguration.PG_PATH + "/*");
    }
}