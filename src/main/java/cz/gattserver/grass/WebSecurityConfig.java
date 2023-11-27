package cz.gattserver.grass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

/*
 * https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
 */
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	private RememberMeServices rememberMeServices;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(List<AuthenticationProvider> authenticationProviders) {
		return new ProviderManager(authenticationProviders);
	}

	@Bean
	public SecurityContextRepository securityContextRepository() {
		return new DelegatingSecurityContextRepository(
				new RequestAttributeSecurityContextRepository(),
				new HttpSessionSecurityContextRepository()
		);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {
		// Pokud se tady nechá odkaz na login, tak se Vaadin nedostane na
		// widgetset na login page (místo JSON response přijde login page...)
		// Všechny možné ostatní postupy selhaly, takže to je nakonec udělané
		// tak, že se přihlašování udělá programaticky. To ale nefunguje bez
		// springSecurityFilterChain ... a ten zase chce http definici ... a ta
		// chce aspoň jeden vstupní bod.
		http.authorizeRequests().requestMatchers(AntPathRequestMatcher.antMatcher("/**")).permitAll();
		http.logout(c -> c.logoutUrl("/logout").logoutSuccessUrl("/").deleteCookies("JSESSIONID").deleteCookies(
				"remember-me").invalidateHttpSession(true));
		// https://docs.spring.io/spring-security/reference/5.8/migration/servlet/session-management.html#_require_explicit_saving_of_securitycontextrepository
		// https://stackoverflow.com/questions/75618616/auto-login-after-registration-spring-boot-3-spring-security-6
		http.securityContext((securityContext) -> securityContext.requireExplicitSave(true).securityContextRepository(securityContextRepository));
		// https://www.mkyong.com/spring-security/spring-security-remember-me-example/
		http.rememberMe(c -> c.rememberMeServices(rememberMeServices).key(SecurityConfig.REMEMBER_ME_KEY));
		http.csrf(c -> c.disable());

		return http.build();
	}
}