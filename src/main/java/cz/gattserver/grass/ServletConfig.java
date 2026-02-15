package cz.gattserver.grass;

import cz.gattserver.grass.articles.AttachmentsRequestHandler;
import cz.gattserver.grass.articles.config.ArticlesConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

public class ServletConfig {

    @Bean
    public ServletRegistrationBean<AttachmentsRequestHandler> attachmentsServlet(
            AttachmentsRequestHandler handler) {
        return new ServletRegistrationBean<>(handler, "/" + ArticlesConfiguration.ATTACHMENTS_PATH + "/*");
    }
}