package cz.gattserver.grass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.RememberMeServices;

/*
 * http://www.baeldung.com/spring-security-remember-me
 * http://www.baeldung.com/spring-security-login
 * 
 * Deprecated - nově je
 * https://www.mkyong.com/spring-security/spring-security-password-hashing-example/
 * 
 * https://howtodoinjava.com/spring5/security5/security-java-config-enablewebsecurity-example/
 * https://stackoverflow.com/questions/12303894/spring-annotation-equivalent-of-securityauthentication-manager-and-securitygl
 * https://stackoverflow.com/questions/25785018/springsecurity-remembermeservices-is-not-injected-via-annotations
 * https://www.mkyong.com/spring-security/spring-security-remember-me-example/
 * https://docs.spring.io/spring-security/site/docs/3.0.x/reference/remember-me.html
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationProvider authenticationProvider;

	@Autowired
	private RememberMeServices rememberMeServices;

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider);
		super.configure(auth);
	}

	@Bean(name = "authenticationManager")
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Pokud se tady nechá odkaz na login, tak se Vaadin nedostane na
		// widgetset na login page (místo JSON response přijde login page...)
		// Všechny možné ostatní postupy selhaly, takže to je nakonec udělané
		// tak, že se přihlašování udělá programaticky. To ale nefunguje bez
		// springSecurityFilterChain ... a ten zase chce http definici ... a ta
		// chce aspoň jeden vstupní bod.
		http.authorizeRequests().antMatchers("/**").permitAll();
		// http.formLogin().loginPage("/loginpage");
		http.logout().logoutSuccessUrl("/").deleteCookies("JSESSIONID");
		// https://www.mkyong.com/spring-security/spring-security-remember-me-example/
		http.rememberMe().rememberMeServices(rememberMeServices).key(SecurityConfig.REMEMBER_ME_KEY);
		http.csrf().disable();
	}

}