package cz.gattserver.grass.fm;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FMRequestHandlerConfig {

    public static final String FM_PATH = "fm-files";

    @Bean
    public ServletRegistrationBean<FMRequestHandler> fmRequestHandlerRegisterBean(FMRequestHandler handler) {
        return new ServletRegistrationBean<>(handler,  "/" + FM_PATH + "/*");
    }
}