package com.backendoori.ootw.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class HttpRequestsConfigurer
    implements Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {

    private static final String AUTH_RESOURCE = "/api/v1/auth/**";
    private static final String POST_RESOURCE = "/api/v1/posts/**";

    @Override
    public void customize(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizeRequests) {
        authorizeRequests
            .requestMatchers(AUTH_RESOURCE)
            .permitAll()
            .requestMatchers(antMatcher(HttpMethod.GET, POST_RESOURCE))
            .permitAll()
            .anyRequest()
            .authenticated();
    }

}
