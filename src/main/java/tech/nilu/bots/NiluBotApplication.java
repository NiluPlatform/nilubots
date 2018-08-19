package tech.nilu.bots;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import tech.nilu.bots.filter.LoggingFilter;

import javax.servlet.DispatcherType;
import java.sql.DriverManager;
import java.util.EnumSet;

@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(value = {"tech.nilu.bots"}
        , excludeFilters = {@ComponentScan.Filter(type = FilterType.REGEX,
        pattern = "tech.nilu.bots.noscan.*")})
@EnableScheduling
public class NiluBotApplication extends SpringBootServletInitializer {


    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("i18n/message");
        source.setDefaultEncoding("UTF-8");
        return source;
    }

    @Bean
    public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
        return new PersistenceAnnotationBeanPostProcessor();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public FilterRegistrationBean loggingFilterMapping(LoggingFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    public static void main(String[] args) throws Exception {
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        SpringApplication.run(NiluBotApplication.class, args);
    }

    @Bean
    public Instagram4j instagram4j(@Value("${integration.bots.insta.username}") String user,
                                   @Value("${integration.bots.insta.password}") String password) {
        Instagram4j instagram = Instagram4j.builder().username(user).password(password).build();
        instagram.setup();
        return instagram;
    }
}
