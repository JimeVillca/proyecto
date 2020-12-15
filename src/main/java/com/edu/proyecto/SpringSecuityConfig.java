package com.edu.proyecto;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import com.edu.proyecto.app.auth.handler.LoginSuccessHandler;


@Configuration
public class SpringSecuityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private DataSource datasource;
	
	@Autowired
	private LoginSuccessHandler successHandler;
	
	@Autowired
	private BCryptPasswordEncoder passEncoder;
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http.authorizeRequests().antMatchers("/","/css/**", "/js/**", "/imagenes/**","/olvidepassword").permitAll()
		.antMatchers("/listar/**").hasAnyRole("ADMIN")
		.antMatchers("/form/**").hasAnyRole("ADMIN")
		.antMatchers("/ver/**").hasAnyRole("ADMIN")
		.antMatchers("/eliminar/**").hasAnyRole("ADMIN")


		.antMatchers("/listarcategoria/**").hasAnyRole("ADMIN")
		.antMatchers("/formcategoria/**").hasAnyRole("ADMIN")
		.antMatchers("/eliminararchivos/**").hasAnyRole("ADMIN")

		.antMatchers("/listararchivos/**").hasAnyRole("ADMIN")
		.antMatchers("/formarchivo/**").hasAnyRole("ADMIN")
		.antMatchers("/eliminararchivos/**").hasAnyRole("ADMIN")
		
		.antMatchers("/listararchivo/**").hasAnyRole("ADMIN")

		.antMatchers("/eliminararchivos/**").hasAnyRole("ADMIN")

		.antMatchers("/listar/**").hasAnyRole("ADMIN")
		.antMatchers("/form/**").hasAnyRole("ADMIN")
		.antMatchers("/ver/**").hasAnyRole("ADMIN")
		
		.antMatchers("/uploads/**").hasAnyRole("USER")
		.antMatchers("/form/**").hasAnyRole("ADMIN")
		.antMatchers("/eliminar/**").hasAnyRole("ADMIN")
				
//		.antMatchers("/nuevapass/**").hasAnyRole("INIT")
		.anyRequest().authenticated()
		.and()
		.formLogin()
		.successHandler(successHandler)
		.loginPage("/login")
		.permitAll()
		.and()
		.logout().permitAll()
		.and()
		.exceptionHandling().accessDeniedPage("/error_403");

	}

	@Autowired
	public void configurerGlobal (AuthenticationManagerBuilder builder) throws Exception {
		
		builder.jdbcAuthentication()
		.dataSource(datasource)
		.passwordEncoder(passEncoder)
		.usersByUsernameQuery("SELECT username,password, enable FROM usuario WHERE username=?")
		.authoritiesByUsernameQuery("SELECT u.username, r.rol FROM rol r, usuario u WHERE u.idrol=r.idrol AND u.username=?")
		;
		
		
	/*	UserBuilder users = User.withDefaultPasswordEncoder();
		builer.inMemoryAuthentication()
		.withUser(users.username("admin").password("12345").roles("ADMIN", "USER"))
		.withUser(users.username("jimena").password("12345").roles("USER"));*/
		
	}



}
