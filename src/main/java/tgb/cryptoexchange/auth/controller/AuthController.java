package tgb.cryptoexchange.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgb.cryptoexchange.auth.dto.UserCredentialsDTO;
import tgb.cryptoexchange.auth.service.AuthService;
import tgb.cryptoexchange.auth.service.UserService;
import tgb.cryptoexchange.web.ApiResponse;
import tgb.cryptoexchange.web.LogResponseBody;

import java.util.List;

@RestController
@RequestMapping("/auth")
@LogResponseBody
@Slf4j
public class AuthController {

    private final AuthService authService;

    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
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
        log.debug("Запрос на регистрацию нового пользователя: {}", credentials.toString());
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

    @Operation(summary = "Получение всех юзернеймов.",
            description = "Возвращает список всех юзернеймов зарегистрированных пользователей.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Список юзернеймов сформирован."
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<String>>> getUsers() {
         return new ResponseEntity<>(ApiResponse.success(userService.getUsernames()), HttpStatus.OK);
    }

    @Operation(summary = "Удаление пользователя по username.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204", description = "Пользователь успешно удален."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Пользователь по данному юзернейму не найден."
            )
    })
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String username) {
        userService.delete(username);
    }

    @PatchMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patch(@PathVariable String username, @RequestParam String password) {
        userService.updatePassword(username, password);
    }
}
