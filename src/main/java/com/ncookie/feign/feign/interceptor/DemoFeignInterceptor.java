package com.ncookie.feign.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor(staticName = "of")
public class DemoFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {

        // GET 요청일 경우
        if (requestTemplate.method().equals("GET")) {
            System.out.println("[GET][DemoFeignInterceptor] queries: " + requestTemplate.queries());
            return;
        }

        // POST 요청일 경우
        String encodedRequestBody = StringUtils.toEncodedString(requestTemplate.body(), StandardCharsets.UTF_8);
        System.out.println("[POST][DemoFeignInterceptor] encodedRequestBody: " + encodedRequestBody);

        // 인코딩한 값을 한 번 변환하여 request에 세팅
        String convertedRequestBody = encodedRequestBody;       // do something
        requestTemplate.body(convertedRequestBody);
    }
}
