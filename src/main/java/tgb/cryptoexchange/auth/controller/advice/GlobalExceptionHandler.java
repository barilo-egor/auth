package tgb.cryptoexchange.auth.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tgb.cryptoexchange.auth.exception.LoginException;
import tgb.cryptoexchange.auth.exception.UsernameAlreadyTakenException;
import tgb.cryptoexchange.web.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<ApiResponse<Void>> handlerLoginException(LoginException e) {
        return new ResponseEntity<>(
                ApiResponse.error(ApiResponse.Error.builder().message(e.getMessage()).build()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<ApiResponse<Void>> handlerUsernameAlreadyTakenException(UsernameAlreadyTakenException e) {
        return new ResponseEntity<>(
                ApiResponse.error(ApiResponse.Error.builder().message(e.getMessage()).build()),
                HttpStatus.CONFLICT
        );
    }
}
