package cz.gattserver.grass.campgames;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CampgamesRequestHandlerConfig {

    public static final String CAMPGAMES_PATH = "campgames-files";

    @Bean
    public ServletRegistrationBean campgamesRequestHandlerRegisterBean(CampgamesRequestHandler handler) {
        return new ServletRegistrationBean<>(handler, "/" + CAMPGAMES_PATH + "/*");
    }
}