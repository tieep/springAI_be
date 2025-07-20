package com.bootcamptoprod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

@SpringBootApplication
public class SpringAIImageAnalysisCookbookApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAIImageAnalysisCookbookApplication.class, args);
    }

    @Bean
    public RestClientCustomizer restClientCustomizer(Logbook logbook) {
        return restClientBuilder -> restClientBuilder.requestInterceptor(new LogbookClientHttpRequestInterceptor(logbook));
    }

}
