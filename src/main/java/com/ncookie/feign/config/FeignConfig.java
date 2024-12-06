package com.ncookie.feign.config;

import com.ncookie.feign.feign.logger.FeignCustomLogger;
import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// DemoFeignConfig와는 다르게 모든 client에게 적용되는 global config다.
// 모든 client에게 일괄적으로 설정을 하고 싶다면 이 클래스를 사용하자.
@Configuration
@EnableFeignClients(        // 스프링에서 Feign client를 사용하기 위한 어노테이션
        basePackages = "com.ncookie.feign",
        defaultConfiguration = FeignConfig.class
)
public class FeignConfig {

    @Bean
    public Logger feignLogger() {
        return new FeignCustomLogger();
    }

    // yml에 작성한 loggerLevel 설정이 동작하지 않아 여기서 설정함
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.HEADERS;
    }

}
