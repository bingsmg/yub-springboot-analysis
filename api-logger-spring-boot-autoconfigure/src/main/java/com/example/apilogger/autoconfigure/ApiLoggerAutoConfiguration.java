package com.example.apilogger.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Servlet;

/**
 * API日志记录自动配置类
 * 
 * @author 示例开发者
 * @since 1.0.0
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({Servlet.class, ApiLoggerInterceptor.class})
@ConditionalOnProperty(name = "api-logger.enabled", matchIfMissing = true)
@EnableConfigurationProperties(ApiLoggerProperties.class)
@AutoConfigureAfter(JacksonAutoConfiguration.class)
public class ApiLoggerAutoConfiguration {

    /**
     * 创建 API 日志记录拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public ApiLoggerInterceptor apiLoggerInterceptor(ApiLoggerProperties properties, ObjectMapper objectMapper) {
        return new ApiLoggerInterceptor(properties, objectMapper);
    }

    /**
     * Web MVC 配置器，用于注册拦截器
     */
    @Configuration
    @ConditionalOnClass(WebMvcConfigurer.class)
    static class ApiLoggerWebMvcConfiguration implements WebMvcConfigurer {

        private final ApiLoggerInterceptor apiLoggerInterceptor;

        public ApiLoggerWebMvcConfiguration(ApiLoggerInterceptor apiLoggerInterceptor) {
            this.apiLoggerInterceptor = apiLoggerInterceptor;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            // 注册拦截器，拦截所有请求
            registry.addInterceptor(apiLoggerInterceptor)
                    .addPathPatterns("/**")
                    .order(0); // 设置为最高优先级
        }
    }
} 