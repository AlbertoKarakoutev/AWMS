package com.company.awms.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	private final UserDetailsService userDetailsService;

	public SecurityConfiguration(@Qualifier("employeeDetailsService") UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		http.addFilterBefore(new CaptchaAuthenticationFilter("/login", "/login?error"),
//				UsernamePasswordAuthenticationFilter.class);

		http.authorizeRequests().antMatchers("/login").permitAll().antMatchers("/css/**/**").permitAll().antMatchers("/js/**").permitAll()
		.antMatchers("/webjars/**").permitAll().antMatchers("/").authenticated()
		.antMatchers("/**").authenticated().antMatchers("/admin/**").hasAuthority("ADMIN").and().csrf()
		.disable().formLogin().defaultSuccessUrl("/").and().formLogin().loginPage("/login")
		.defaultSuccessUrl("/").and().logout().logoutSuccessUrl("/login").and().requiresChannel().anyRequest()
		.requiresSecure();
	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
