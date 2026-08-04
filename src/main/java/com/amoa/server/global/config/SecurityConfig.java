package com.amoa.server.global.config;

import com.amoa.server.global.auth.AuthenticationEntryPointImpl;
import com.amoa.server.global.auth.filter.JwtAuthFilter;
import com.amoa.server.global.auth.filter.JwtExceptionFilter;
import org.springframework.http.HttpMethod;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final AuthenticationEntryPointImpl authenticationEntryPointImpl;

    private final String[] allowUris = {
            // Swagger 허용
            "/swagger-ui/**",
            "/v3/api-docs/**",

            //공통
            "/error",
            "/api/v1/auth/kakao",
            "/api/v1/auth/reissue",

            // 인증 관련해서는 jwt 토큰 인증 없이도 요청을 보낼 수 있어야 함
            "/health"
    };

    private final String[] getAllowUris = {
            "/api/v1/regions/present",
            "/api/v1/regions",
            "/api/v1/cards",
            "/api/v1/cards/home",
            "/api/v1/cards/*",
            "/api/v1/cards/*/recommendations",
            "/api/v1/terms/**",
            "/api/v1/shops/design-tags"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // csrf 보호 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // http basic 인증 방식 비활성화
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS)) // jwt 기반 인증을 사용하므로, 세션을 생성하지 않게끔(stateless 방식으로 설정)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authenticationEntryPointImpl)) // 인증 실패 시 처리(예외 처리 설정)
                .authorizeHttpRequests(requests -> requests.requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()// CORS preflight 요청은 인증 없이 허용
                        .requestMatchers(allowUris).permitAll() // 허용된 uri는 접근 가능
                        .requestMatchers(HttpMethod.GET, getAllowUris).permitAll()
                        .anyRequest().authenticated()) // 그 외 요청은 반드시 인증 필요 명시
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class) // UsernamePasswordAuthenticationFilter 이전에 JwtAuthFilter를 먼저 실행
                .addFilterBefore(jwtExceptionFilter,
                        JwtAuthFilter.class); // JwtAuthFilter 이전에 JwtExceptionFilter를 먼저 실행(jwt 관련 예외 처리)

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost:5177",
                "https://amoa-frontend-git-dev-hunbee776s-projects.vercel.app",
                "https://amoa-frontend.vercel.app",

                // Swagger가 새 백엔드 도메인에서 실행되므로 추가
                "https://api.amoa.beauty",

                //새 프론트 배포 주소
                "https://www.amoa.beauty"
        ));

        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
