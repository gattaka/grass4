package cz.gattserver.grass.articles.plugins.favlink.server;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FavlinkImageRequestHandlerConfig {

    public static final String IMAGE_PATH_ALIAS = "articles-favlink-plugin";

    @Bean
    public ServletRegistrationBean favlinkImageRequestHandlerRegistrationBean(FavlinkImageRequestHandler handler) {
        return new ServletRegistrationBean<>(handler, "/" + IMAGE_PATH_ALIAS + "/*");
    }
}