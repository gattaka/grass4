package cz.gattserver.grass.articles.plugins.favlink.server;

import cz.gattserver.grass.articles.plugins.favlink.config.FavlinkConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FavlinkImageRequestHandlerConfig {

    @Bean
    public ServletRegistrationBean favlinkImageRequestHandlerRegistrationBean(FavlinkImageRequestHandler handler) {
        return new ServletRegistrationBean<>(handler, "/" + FavlinkConfiguration.IMAGE_PATH_ALIAS + "/*");
    }
}