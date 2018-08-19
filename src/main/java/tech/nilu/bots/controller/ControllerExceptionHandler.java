package tech.nilu.bots.controller;

import tech.nilu.bots.exception.ApplicationException;
import tech.nilu.bots.dto.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created by mmariameda on 9/26/16.
 */
@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<BaseResponse> business(ApplicationException e) {
        BaseResponse ret = new BaseResponse(e);

        return ResponseEntity.status(ret.getStatus()).body(ret);
    }

}
