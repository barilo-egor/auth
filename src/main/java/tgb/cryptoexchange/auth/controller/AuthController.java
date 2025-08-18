package tgb.cryptoexchange.auth.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgb.cryptoexchange.auth.dto.UserCredentialsDTO;
import tgb.cryptoexchange.auth.exception.AuthException;
import tgb.cryptoexchange.auth.service.AuthService;
import tgb.cryptoexchange.controller.ApiController;
import tgb.cryptoexchange.web.ApiResponse;

@RestController
@RequestMapping("/auth")
public class AuthController extends ApiController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody UserCredentialsDTO credentials) {
        if (!credentials.isValidForRegistration()) {
            return new ResponseEntity<>(
                    ApiResponse.error(ApiResponse.Error.builder().message("Invalid data").build()),
                    HttpStatus.BAD_REQUEST
            );
        }
        String token = authService.register(credentials.getUsername(), credentials.getPassword());
        return new ResponseEntity<>(
                ApiResponse.success(token),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody UserCredentialsDTO credentials) {
        return new ResponseEntity<>(
                ApiResponse.success(
                        authService.login(credentials.getUsername(), credentials.getPassword())
                ),
                HttpStatus.CREATED
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(EntityNotFoundException e) {
        return new ResponseEntity<>(
                ApiResponse.error(
                        ApiResponse.Error.builder()
                                .code(ApiResponse.Error.ErrorCode.ENTITY_NOT_FOUND)
                                .message(e.getMessage())
                                .build()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthException(AuthException e) {
        return new ResponseEntity<>(
                ApiResponse.error(
                        ApiResponse.Error.builder()
                                .message(e.getMessage())
                                .build()
                ),
                HttpStatus.FORBIDDEN
        );
    }
}
