package cz.gattserver.common.spring;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public class SpringContextHelper {

	private static ApplicationContext applicationContext;

	private SpringContextHelper() {
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextHelper.applicationContext = applicationContext;
	}

	public static ApplicationContext getContext() {
		return applicationContext;
	}

	public static Object getBean(final String beanRef) {
		return getContext().getBean(beanRef);
	}

	public static <T> T getBean(final Class<T> type) {
		return getContext().getBean(type);
	}

	public static void inject(Object target) {
		getContext().getAutowireCapableBeanFactory().autowireBeanProperties(target,
				AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
	}

}