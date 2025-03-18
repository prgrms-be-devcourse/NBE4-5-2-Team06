package org.example.bidflow.global.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() { // Security가 기대하는 CorsFilter
        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://35.203.149.35:3000");  // 배포 주소 추가
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }


}
