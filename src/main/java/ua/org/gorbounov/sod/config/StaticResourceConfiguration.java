package ua.org.gorbounov.sod.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {

	@Value("${prom.ua.products.import.images.local.path}")
	private String myExternalFilePath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		// myExternalFilePath =
		// "/home/gk/eclipse-workspace/spring-mvc/sod/prom/images"+"/";
		System.out.println("WebMvcConfigurer - addResourceHandlers() function get loaded..." + myExternalFilePath);
		registry.addResourceHandler("/img/**").addResourceLocations("file:" + myExternalFilePath + "/");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
		resolver.setOneIndexedParameters(true);
		resolvers.add(resolver);
		WebMvcConfigurer.super.addArgumentResolvers(resolvers);
	}
// addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers)

}
