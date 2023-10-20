package cz.gattserver.grass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

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
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// Pokud se tady nechá odkaz na login, tak se Vaadin nedostane na
		// widgetset na login page (místo JSON response přijde login page...)
		// Všechny možné ostatní postupy selhaly, takže to je nakonec udělané
		// tak, že se přihlašování udělá programaticky. To ale nefunguje bez
		// springSecurityFilterChain ... a ten zase chce http definici ... a ta
		// chce aspoň jeden vstupní bod.
		http.authorizeRequests().requestMatchers(AntPathRequestMatcher.antMatcher("/**")).permitAll();
		// http.formLogin().loginPage("/loginpage");
		http.logout(c -> c.logoutSuccessUrl("/").deleteCookies("JSESSIONID"));
		// https://www.mkyong.com/spring-security/spring-security-remember-me-example/
		http.rememberMe(c -> c.rememberMeServices(rememberMeServices).key(SecurityConfig.REMEMBER_ME_KEY));
		http.csrf(c -> c.disable());

		//http.authorizeHttpRequests((authz) -> authz.anyRequest().authenticated()).httpBasic(withDefaults());
		return http.build();
	}

}