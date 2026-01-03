package kr.hhplus.be.server.config.interceptor;

import kr.hhplus.be.server.support.interceptor.WaitingTokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final WaitingTokenInterceptor waitingTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(waitingTokenInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/health", "/api/public/**");
    }
}
