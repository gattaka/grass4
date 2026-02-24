package cz.gattserver.grass.hw;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HWRequestHandlerConfig {

    public static final String HW_PATH = "hw-files";

    @Bean
    public ServletRegistrationBean<HWRequestHandler> hwRequestHandlerRegisterBean(HWRequestHandler handler) {
        return new ServletRegistrationBean<>(handler, "/" + HW_PATH + "/*");
    }
}