package com.smart.config;

import javax.transaction.Transactional;

//import javax.transaction.Transactional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}
	///configure methods.....
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.authenticationProvider(authenticationProvider());
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN").and()
		.formLogin().loginPage("/login")
		.loginProcessingUrl("/dologin")
		.defaultSuccessUrl("/user/index")
//		.failureUrl("/login-fail")
		.and().csrf().disable();
//		.and().antMatcher("/user/**").hasRole("User")
		http.authorizeRequests().antMatchers("/user/**").hasRole("USER").and()
		.formLogin().loginPage("/login")
		.loginProcessingUrl("/dologin")
		.defaultSuccessUrl("/user/index")
//		.failureUrl("/login-fail")
		.and().csrf().disable();
		http.authorizeRequests().antMatchers("/**").permitAll().and().formLogin().and().csrf().disable();
	}
	
	
	
	
}
