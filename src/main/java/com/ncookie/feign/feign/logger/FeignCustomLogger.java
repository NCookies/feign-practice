package com.ncookie.feign.feign.logger;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class FeignCustomLogger extends Logger {
    private static final int DEFAULT_SLOW_API_TIME = 3_000;
    private static final String SLOW_API_NOTICE = "Slow API";

    @Override
    protected void log(String configKey, String format, Object... objects) {
        // log를 어떤 형식으로 남길지 정해준다.
        System.out.println(String.format(methodTag(configKey) + format, objects));
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        /**
         * [값]
         * configKey = DemoFeignClient#callGet(String,String,Long)
         * logLevel = HEADERS # "feign.client.config.demo-client.loggerLevel" 참고
         *
         * [동작 순서]
         * `logRequest` 메소드 진입 -> 외부 요청 -> `logAndRebufferResponse` 메소드 진입
         *
         * [참고]
         * request에 대한 정보는
         * `logAndRebufferResponse` 메소드 파라미터인 response에도 있다.
         * 그러므로 request에 대한 정보를 [logRequest, logAndRebufferResponse] 중 어디에서 남길지 정하면 된다.
         * 만약 `logAndRebufferResponse`에서 남긴다면 `logRequest`는 삭제해버리자.
         */
        // request만 핸들링 가능
        System.out.println("[logRequest]: " + request);
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        /**
         * [참고]
         * - `logAndRebufferResponse` 메소드내에선 Request, Response에 대한 정보를 log로 남길 수 있다.
         * - 매소드내 코드는 "feign.Logger#logAndRebufferResponse(java.lang.String, feign.Logger.Level, feign.Response, long)"에서 가져왔다.
         *
         * [사용 예]
         * 예상 요청 처리 시간보다 오래 걸렸다면 "Slow API"라는 log를 출력시킬 수 있다.
         * ex) [DemoFeignClient#callGet] <--- HTTP/1.1 200 (115ms)
         *     [DemoFeignClient#callGet] connection: keep-alive
         *     [DemoFeignClient#callGet] content-type: application/json
         *     [DemoFeignClient#callGet] date: Sun, 24 Jul 2022 01:26:05 GMT
         *     [DemoFeignClient#callGet] keep-alive: timeout=60
         *     [DemoFeignClient#callGet] transfer-encoding: chunked
         *     [DemoFeignClient#callGet] {"name":"customName","age":1,"header":"CustomHeader"}
         *     [DemoFeignClient#callGet] [Slow API] elapsedTime : 3001
         *     [DemoFeignClient#callGet] <--- END HTTP (53-byte body)
         */

        // request, response 모두 핸들링 가능

        // 이미 정의되어 있던 메소드 내용을 그대로 가져왔다.
        // 실제 작업할 때 여기서 원하는 부분을 커스텀하자.

        String protocolVersion = resolveProtocolVersion(response.protocolVersion());
        String reason = response.reason() != null && logLevel.compareTo(Logger.Level.NONE) > 0 ? " " + response.reason() : "";
        int status = response.status();
        this.log(configKey, "<--- %s %s%s (%sms)", protocolVersion, status, reason, elapsedTime);
        if (logLevel.ordinal() >= Logger.Level.HEADERS.ordinal()) {
            for(String field : response.headers().keySet()) {
                if (this.shouldLogResponseHeader(field)) {
                    for(String value : Util.valuesOrEmpty(response.headers(), field)) {
                        this.log(configKey, "%s: %s", field, value);
                    }
                }
            }

            int bodyLength = 0;
            if (response.body() != null && status != 204 && status != 205) {
                if (logLevel.ordinal() >= Logger.Level.FULL.ordinal()) {
                    this.log(configKey, "");
                }

                byte[] bodyData = Util.toByteArray(response.body().asInputStream());
                Util.ensureClosed(response.body());
                bodyLength = bodyData.length;
                if (logLevel.ordinal() >= Logger.Level.FULL.ordinal() && bodyLength > 0) {
                    this.log(configKey, "%s", Util.decodeOrDefault(bodyData, Util.UTF_8, "Binary data"));
                }

                // 통신 시간이 상정한 것보다 오래 걸리면 언젠가는 timeout exception이 발생할 가능성이 높다.
                // 때문에 이런 elapsed time 핸들링을 추가해 위험을 방지해야 한다.
                if (elapsedTime > DEFAULT_SLOW_API_TIME) {
                    log(configKey, "[%s] elaspedTime : %s", SLOW_API_NOTICE, elapsedTime);
                }

                this.log(configKey, "<--- END HTTP (%s-byte body)", bodyLength);
                return response.toBuilder().body(bodyData).build();
            }

            this.log(configKey, "<--- END HTTP (%s-byte body)", bodyLength);
        }

        return response;
    }

}
