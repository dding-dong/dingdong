package com.sparta.dingdong.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sparta.dingdong.common.jwt.JwtAccessDeniedHandler;
import com.sparta.dingdong.common.jwt.JwtAuthenticationEntryPoint;
import com.sparta.dingdong.common.jwt.JwtFilter;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtFilter jwtFilter;
	// private final CustomOAuth2UserService customOAuth2UserService;
	// private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(SecurityUrlMatcher.PUBLIC_URLS).permitAll()
				.requestMatchers(SecurityUrlMatcher.CUSTOMER_URLS).hasRole("CUSTOMER")
				.requestMatchers(SecurityUrlMatcher.OWNER_URLS).hasRole("OWNER")
				.requestMatchers(SecurityUrlMatcher.MANAGER_URLS).hasRole("MANAGER")
				.requestMatchers(SecurityUrlMatcher.MASTER_URLS).hasRole("MASTER")
				.anyRequest().authenticated()
			)
			// .oauth2Login(oauth -> oauth
			// 	.userInfoEndpoint(user -> user.userService(customOAuth2UserService))
			// 	.successHandler(oAuth2LoginSuccessHandler)
			// )
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)  // 인증 실패 (401)
				.accessDeniedHandler(jwtAccessDeniedHandler)            // 인가 실패 (403)
			);

		return http.build();
	}

}
