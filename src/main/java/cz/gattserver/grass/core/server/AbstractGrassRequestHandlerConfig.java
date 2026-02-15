package cz.gattserver.grass.core.server;

import org.springframework.boot.web.servlet.ServletRegistrationBean;

public interface AbstractGrassRequestHandlerConfig {

    <T extends AbstractGrassRequestHandler> ServletRegistrationBean<T> registerBean();
}
