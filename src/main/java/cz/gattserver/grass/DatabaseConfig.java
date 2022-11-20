package cz.gattserver.grass;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
 * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/annotation/EnableTransactionManagement.html
 * https://docs.spring.io/spring/docs/4.2.x/spring-framework-reference/html/transaction.html
 * https://stackoverflow.com/questions/47635650/spring-data-jpa-how-to-programmatically-set-jparepository-base-packages
 */
@EnableTransactionManagement
public class DatabaseConfig {

	@Value("${hibernate.connection.driver_class}")
	private String driverClassName;

	@Value("${hibernate.connection.url}")
	private String url;

	@Value("${hibernate.connection.username}")
	private String username;

	@Value("${hibernate.connection.password}")
	private String password;

	@Value("${hibernate.dialect}")
	private String hibernateDialect;

	@Value("${hibernate.show_sql:false}")
	private String hibernateShowSql;

	@Value("${hibernate.format_sql:false}")
	private String hibernateFormatSql;

	@Value("${hibernate.use_sql_comments:false}")
	private String hibernateUseSqlComments;

	@Value("${hibernate.hbm2ddl.auto:none}")
	private String hibernateHbm2ddlAuto;

	@Bean(name = "dataSource")
	public DataSource dataSource() {
		// org.apache.tomcat.jdbc.pool.DataSource musí vidět na driver - protože
		// je to ale volané z Tomcat classloaderu musí se dodat přímo do
		// serveru, viz.
		// http://stackoverflow.com/questions/13161747/jdbc-apache-tomcat-pooling-jar
		DataSource dataSource = new DataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setValidationInterval(5000);
		dataSource.setTestOnBorrow(true);
		dataSource.setTestWhileIdle(true);
		return dataSource;
	}

	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setPersistenceXmlLocation("classpath:jpa/jpa-persistence.xml");
		bean.setDataSource(dataSource);
		bean.setPackagesToScan("cz.gattserver.grass");
		bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", hibernateDialect);
		jpaProperties.setProperty("hibernate.show_sql", hibernateShowSql);
		jpaProperties.setProperty("hibernate.format_sql", hibernateFormatSql);
		jpaProperties.setProperty("hibernate.use_sql_comments", hibernateUseSqlComments);
		jpaProperties.setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
		bean.setJpaProperties(jpaProperties);

		return bean;
	}

	@Bean(name = "transactionManager")
	public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

}
