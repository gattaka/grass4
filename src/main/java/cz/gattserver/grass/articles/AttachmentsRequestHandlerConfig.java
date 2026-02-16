package cz.gattserver.grass.articles;

import cz.gattserver.grass.articles.config.ArticlesConfiguration;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AttachmentsRequestHandlerConfig {

    @Bean
    public ServletRegistrationBean attachmentsRequestHandlerRegisterBean(AttachmentsRequestHandler handler) {
        return new ServletRegistrationBean<>(handler, "/" + ArticlesConfiguration.ATTACHMENTS_PATH + "/*");
    }
}