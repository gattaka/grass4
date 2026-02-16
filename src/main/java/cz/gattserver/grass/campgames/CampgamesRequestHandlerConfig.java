package cz.gattserver.grass.campgames;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CampgamesRequestHandlerConfig {

    @Bean
    public ServletRegistrationBean campgamesRequestHandlerRegisterBean(CampgamesRequestHandler handler) {
        return new ServletRegistrationBean<>(handler, "/" + CampgamesConfiguration.CAMPGAMES_PATH + "/*");
    }
}