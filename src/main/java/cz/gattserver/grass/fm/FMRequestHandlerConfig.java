package cz.gattserver.grass.fm;

import cz.gattserver.grass.fm.config.FMConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FMRequestHandlerConfig {

    @Bean
    public ServletRegistrationBean fmRequestHandlerRegisterBean(FMRequestHandler handler) {
        return new ServletRegistrationBean<>(handler,  "/" + FMConfiguration.FM_PATH + "/*");
    }
}