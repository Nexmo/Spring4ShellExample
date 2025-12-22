package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main Spring Boot Application Class
 * 
 * CVE-2022-22965 REQUIREMENT: WAR Deployment
 * ==========================================
 * 
 * For this vulnerability to be exploitable, the application must be 
 * deployed as a WAR file on Apache Tomcat (not embedded Tomcat).
 * 
 * This is because the exploit targets Tomcat-specific classes:
 * - org.apache.catalina.loader.WebappClassLoaderBase
 * - org.apache.catalina.core.StandardContext
 * - org.apache.catalina.valves.AccessLogValve
 * 
 * These classes are accessible through the classloader chain only
 * when running on external Tomcat with WAR deployment.
 * 
 * SpringBootServletInitializer:
 * This class is extended to support WAR deployment. When deployed 
 * as a WAR, Tomcat calls the configure() method instead of main().
 */

@SpringBootApplication
public class HandlingFormSubmissionApplication extends SpringBootServletInitializer  {

	public static void main(String[] args) {
		SpringApplication.run(HandlingFormSubmissionApplication.class, args);
	}

}