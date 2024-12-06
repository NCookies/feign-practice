package com.ncookie.feign.feign.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class DemoFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.resolve(response.status());

        // 외부 컴포넌트와 통신 시 정의해놓은 예외 코드일 경우엔 적절하게 핸들링하여 처리한다.
        if (httpStatus == HttpStatus.NOT_FOUND) {
            System.out.println("[DemoFeignErrorDecoder] Http Status = " + httpStatus);
            throw new RuntimeException(String.format("[RuntimeException] Http Status is %s", httpStatus));
        }

        // 직접 핸들링 하지 않은 에러들은 그대로 전달한다.
        // 실제 환경에서는 ControllerAdvice 등에서 한 번 wrapping한 exception들을 처리한다.
        return errorDecoder.decode(methodKey, response);
    }

}
