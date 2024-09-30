package edu.wlu.graffiti;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import edu.wlu.graffiti.filters.AuthenticationFilter;
import edu.wlu.graffiti.filters.LogFilter;

@SpringBootApplication
@EnableCaching
public class Application extends SpringBootServletInitializer {

	@Value("${accesslog.log-requests:true}")
	private boolean includeRequestLog;
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
	
	@Bean
	public FilterRegistrationBean<AuthenticationFilter> authenticationFilter() {

		FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(new AuthenticationFilter());
		registrationBean.addUrlPatterns("/admin/*");
		registrationBean.addUrlPatterns("/indices/*");
		return registrationBean;
	}
	
	/*
	@Bean
	@ConditionalOnExpression("${accesslog.log-requests:true}")
	public FilterRegistrationBean<LogFilter> loggingFilter() {
		System.out.println("Starting LogFilter...");

		FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(new LogFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}
	*/
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}