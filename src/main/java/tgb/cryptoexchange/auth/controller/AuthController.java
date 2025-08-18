package tgb.cryptoexchange.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tgb.cryptoexchange.auth.dto.UserCredentialsDTO;
import tgb.cryptoexchange.auth.service.AuthService;
import tgb.cryptoexchange.web.ApiResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

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
        if (!credentials.isValidForLogin()) {
            return new ResponseEntity<>(
                    ApiResponse.error(ApiResponse.Error.builder().message("Invalid data").build()),
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(
                ApiResponse.success(
                        authService.login(credentials.getUsername(), credentials.getPassword())
                ),
                HttpStatus.CREATED
        );
    }
}
