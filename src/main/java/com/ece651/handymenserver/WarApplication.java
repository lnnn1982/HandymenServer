package com.ece651.handymenserver;

import javax.servlet.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.ece651.handymenserver.storage.*;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class WarApplication extends SpringBootServletInitializer {
	static private ServletContext servletContext;
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WarApplication.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(WarApplication.class, args);
	}
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		System.out.println("WarApplication onStartup");
		WarApplication.servletContext = servletContext;
 		super.onStartup(servletContext);
	}
	
    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
             storageService.initPath(WarApplication.servletContext);
        };
    }

}
