package com.ncookie.feign.controller;

import com.ncookie.feign.service.DemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemoController {

    private final DemoService demoService;

    @GetMapping("/get")
    public String getController() {
        return demoService.get();
    }

    @GetMapping("/post")
    public String postController() {
        return demoService.post();
    }

    @GetMapping("/error")
    public String errorDecoderController() {
        return demoService.errorDecoder();
    }

}
