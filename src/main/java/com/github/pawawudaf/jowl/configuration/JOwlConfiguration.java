package com.github.pawawudaf.jowl.configuration;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JOwlConfiguration {

    @Bean
    public UrlValidator urlValidator() {
        return new UrlValidator();
    }
}
