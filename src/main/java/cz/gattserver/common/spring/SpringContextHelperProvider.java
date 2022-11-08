package cz.gattserver.common.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHelperProvider implements ApplicationContextAware {

	public void setApplicationContext(ApplicationContext ctx) {
		SpringContextHelper.setApplicationContext(ctx);
	}
}