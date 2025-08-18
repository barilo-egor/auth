package tgb.cryptoexchange.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Регистрация нового пользователя.", description = "Возвращает JWT в случае успешной регистрации.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Пользователь зарегистрирован."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Данные невалидны."
            )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового пользователя", required = true,
                    content = @Content(schema = @Schema(implementation = UserCredentialsDTO.class))
            )
            @RequestBody UserCredentialsDTO credentials) {
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

    @Operation(summary = "Аутентификация.", description = "Возвращает JWT в случае успешной аутентификации.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Пользователь зарегистрирован."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Данные невалидны."
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для аутентификации.", required = true,
                    content = @Content(schema = @Schema(implementation = UserCredentialsDTO.class))
            )
            @RequestBody UserCredentialsDTO credentials) {
        if (!credentials.isValidForLogin()) {
            return new ResponseEntity<>(
                    ApiResponse.error(ApiResponse.Error.builder().message("Invalid data").build()),
                    HttpStatus.FORBIDDEN
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
