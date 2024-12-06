package com.ncookie.feign.service;

import com.ncookie.feign.common.dto.BaseRequestInfo;
import com.ncookie.feign.common.dto.BaseResponseInfo;
import com.ncookie.feign.feign.client.DemoFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DemoService {

    private final DemoFeignClient demoFeignClient;

    public String get() {
        ResponseEntity<BaseResponseInfo> response = demoFeignClient.callGet("CustomHeader", "CustomName", 1L);

        System.out.println("Name: " + response.getBody().getName());
        System.out.println("Age: " + response.getBody().getAge());
        System.out.println("Header: " + response.getBody().getHeader());

        return "get";
    }

    public String post() {
        BaseRequestInfo requestInfo = BaseRequestInfo.builder()
                .name("CustomName")
                .age(2L)

                .build();
        ResponseEntity<BaseResponseInfo> response = demoFeignClient.callPost("CustomHeader", requestInfo);

        System.out.println("Name: " + response.getBody().getName());
        System.out.println("Age: " + response.getBody().getAge());
        System.out.println("Header: " + response.getBody().getHeader());

        return "get";
    }

}
