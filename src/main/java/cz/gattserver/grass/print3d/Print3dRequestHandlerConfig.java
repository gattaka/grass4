package cz.gattserver.grass.print3d;

import cz.gattserver.grass.print3d.config.Print3dConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Print3dRequestHandlerConfig {

    @Bean
    public ServletRegistrationBean print3dRequestHandlerRegisterBean(Print3dRequestHandler handler) {
        return new ServletRegistrationBean<>(handler, "/" + Print3dConfiguration.PRINT3D_PATH + "/*");
    }
}