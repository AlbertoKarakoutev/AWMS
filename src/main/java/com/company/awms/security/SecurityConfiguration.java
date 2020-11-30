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

	public SecurityConfiguration(@Qualifier("customUserDetailsService") UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/login").permitAll()
				.antMatchers("/").permitAll()
				.antMatchers("/document/**").authenticated()
				.antMatchers("/schedule/**").authenticated()
				.antMatchers("/forum/**").authenticated()
				.antMatchers("/salary/**").authenticated()
				.antMatchers("/admin/**").hasAuthority("ADMIN")
				.and().csrf().disable().formLogin().defaultSuccessUrl("/")
				.and().logout().logoutSuccessUrl("/")
				.and().requiresChannel().anyRequest().requiresSecure();
	}

	@Bean
	public PasswordEncoder getPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
