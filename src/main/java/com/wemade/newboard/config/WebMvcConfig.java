package com.wemade.newboard.config;

import com.wemade.newboard.interceptors.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig  implements WebMvcConfigurer {
    private final long MAX_AGE_SECS=3600;

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**") //모든 경로 허락(모든 컨트롤러)
                .allowedOrigins("http://localhost:5173") // 이 Origin이면 허락
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 이 메소드면 허락
                .allowedHeaders("*") //
//                .allowCredentials(true) //쿠키요청을 여부, 보안상 이슈가 발생할 수 있음
                .maxAge(MAX_AGE_SECS); // 원하는 시간만큼 pre-flight 요청에 대한 응답을 브라우저에서 캐싱하는 시간
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/newboard/**")
                .excludePathPatterns("/newboard/public/**") // signup, login, refresh-token, {id}
                .excludePathPatterns("/newboard/posts/public/**") // post/public/** 관련은 제외
        ;

    }
}
