package tgb.cryptoexchange.auth.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgb.cryptoexchange.auth.dto.UserCredentialsDTO;
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
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(
                ApiResponse.success(
                        authService.register(credentials.getUsername(), credentials.getPassword())
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody UserCredentialsDTO credentials) {
        return ApiResponse.success(
                authService.login(credentials.getUsername(), credentials.getPassword())
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ApiResponse<Void> handleEntityNotFound(EntityNotFoundException e) {
        return ApiResponse.error(
                ApiResponse.Error.builder()
                        .code(ApiResponse.Error.ErrorCode.ENTITY_NOT_FOUND)
                        .message(e.getMessage())
                        .build()
        );
    }
}
