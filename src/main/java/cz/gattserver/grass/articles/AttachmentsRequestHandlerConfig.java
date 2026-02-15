package cz.gattserver.grass.articles;

import cz.gattserver.grass.articles.config.ArticlesConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Registrace servletu přesunuta do cz.gattserver.grass.ServletConfig, protože tranzitivně vznikal problém s vytvářením instance jakarta.persistence.EntityManager
@Configuration
public class AttachmentsRequestHandlerConfig {

    @Autowired
    private AttachmentsRequestHandler attachmentsRequestHandler;

    @Bean
    public ServletRegistrationBean registerBean() {
        return new ServletRegistrationBean<>(attachmentsRequestHandler,
                "/" + ArticlesConfiguration.ATTACHMENTS_PATH + "/*");
    }
}