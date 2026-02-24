package cz.gattserver.grass.print3d;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Print3dRequestHandlerConfig {

    public static final String PRINT3D_PATH = "print3d-files";

    @Bean
    public ServletRegistrationBean print3dRequestHandlerRegisterBean(Print3dRequestHandler handler) {
        return new ServletRegistrationBean<>(handler, "/" + PRINT3D_PATH + "/*");
    }
}