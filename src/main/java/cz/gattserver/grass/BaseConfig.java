package cz.gattserver.grass;

import cz.gattserver.grass.core.services.MailService;
import cz.gattserver.grass.core.services.VersionInfoService;
import cz.gattserver.grass.core.services.impl.MailServiceImpl;
import cz.gattserver.grass.core.services.impl.VersionInfoServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/*
 * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Configuration.html
 * https://www.baeldung.com/spring-value-defaults
 * https://dzone.com/articles/defining-bean-dependencies-with-java-config-in-spring-framework
 * https://stackoverflow.com/questions/6904956/spring-configuration-non-xml-configuration-for-annotation-driven-tasks
 * https://docs.spring.io/spring/docs/3.1.x/javadoc-api/org/springframework/scheduling/annotation/ScheduledAnnotationBeanPostProcessor.html
 * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/EnableAspectJAutoProxy.html
 */

@EnableAsync
@EnableScheduling
@ServletComponentScan
@PropertySource({ "classpath:version.properties"})
@Import({ DatabaseConfig.class, EncoderConfig.class, SecurityConfig.class, WebSecurityConfig.class })
public class BaseConfig {

	@Value("${grass.mail.address}")
	private String grassMailAddress;

	@Value("${grass.mail.password}")
	private String grassMailPassword;

	@Value("${grass.notification.address}")
	private String grassNotificationAddress;

	@Value("${version}")
	private String version;

	@Bean(name = "taskExecutor")
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(25);
		return executor;
	}


	@Bean(name = "grassMailService")
	public MailService grassMailService() {
		MailServiceImpl mailService = new MailServiceImpl();
		mailService.setGrassMailAddress(grassMailAddress);
		mailService.setGrassMailPassword(grassMailPassword);
		mailService.setGrassNotificationAddress(grassNotificationAddress);
		return mailService;
	}

	@Bean
	public VersionInfoService versionInfoService() {
		VersionInfoServiceImpl infoService = new VersionInfoServiceImpl();
		infoService.setVersionProperties(version);
		return infoService;
	}

}
