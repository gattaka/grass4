package cz.gattserver.grass.articles;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AttachmentsRequestHandlerConfig {

    public static final String ATTACHMENTS_PATH = "articles-attachments";

    @Bean
    public ServletRegistrationBean<AttachmentsRequestHandler> attachmentsRequestHandlerRegisterBean(AttachmentsRequestHandler handler) {
        return new ServletRegistrationBean<>(handler, "/" + ATTACHMENTS_PATH + "/*");
    }
}