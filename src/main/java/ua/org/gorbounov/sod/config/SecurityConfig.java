/**
 * 
 */
package ua.org.gorbounov.sod.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * @author gk
 *
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AccessDeniedHandler accessDeniedHandler;
	/**
	 *
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
//		successHandler.setTargetUrlParameter("redirectTo");
//		successHandler.setDefaultTargetUrl("/");

		
		/*
		 * http.authorizeRequests().antMatchers("/", "/assets/**",
		 * "/webjars/**","/js/**").permitAll().antMatchers("/login")
		 * .permitAll().anyRequest().authenticated()
		 * .and().formLogin().loginPage("/login").successHandler(successHandler)
		 * .and().logout().logoutUrl("/login") .and().httpBasic() .and().csrf()
		 * .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		 * .ignoringAntMatchers("/instances", "/actuator/**");
		 */	
		
	    http.csrf().disable()
        .authorizeRequests()
        .antMatchers("/", "/index", "/prom/**","/rozetka/**", "/about", "/webjars/**","/js/**", "/h2-console/**").permitAll()
        .antMatchers("/admin/**",  "/actuator/**").hasAnyRole("ADMIN")
        .antMatchers("/user/**").hasAnyRole("USER")
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/login")
        .defaultSuccessUrl("/", true)
        .failureUrl("/403")
        .permitAll()
        .and()
        .logout()
        .deleteCookies("JSESSIONID")
        .permitAll()
        .and()
        .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
	    
	    //добавка для работы h2-console
	    http.headers().frameOptions().disable();	}
	
    // создаем пользоватлелей, admin и user
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
 
        auth.inMemoryAuthentication()
                .withUser("user").password("password").roles("USER")
                .and()
                .withUser("admin").password("{noop}admin").roles("ADMIN");
    }

}
