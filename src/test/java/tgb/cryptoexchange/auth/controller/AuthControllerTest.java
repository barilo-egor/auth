package tgb.cryptoexchange.auth.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tgb.cryptoexchange.auth.config.SecurityConfig;
import tgb.cryptoexchange.auth.controller.advice.GlobalExceptionHandler;
import tgb.cryptoexchange.auth.exception.AuthException;
import tgb.cryptoexchange.auth.exception.LoginException;
import tgb.cryptoexchange.auth.exception.UsernameAlreadyTakenException;
import tgb.cryptoexchange.auth.service.AuthService;
import tgb.cryptoexchange.auth.service.UserService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @ParameterizedTest
    @CsvSource({
            "username1,Qwe123!@$",
            "user,123qweQWE!@#",
            "admin,qQ12345&"
    })
    @DisplayName("/auth/register - валидные значения - возвращает токен")
    void registerShouldReturnToken(String username, String password) throws Exception {
        String token = "token";
        when(authService.register(username, password)).thenReturn(token);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(token));
    }

    @Test
    @DisplayName("/auth/register - невалидное значение - возвращает 400")
    void shouldReturn400IfCredentialIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"username\", \"password\":\"qwerty\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("/auth/login - проброшено LoginException - возвращает 403")
    void loginShouldReturn403IfLoginExceptionThrown() throws Exception {
        String username = "username";
        String password = "Qwe123#$%";
        String exceptionMessage = "message";
        when(authService.login(username, password)).thenThrow(new LoginException(exceptionMessage));
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value(exceptionMessage));
    }

    @Test
    @DisplayName("/auth/login - проброшено UsernameAlreadyTakenException - возвращает 403")
    void loginShouldReturn403IfUsernameAlreadyTakenExceptionThrown() throws Exception {
        String username = "username";
        String password = "Qwe123#$%";
        String exceptionMessage = "message";
        when(authService.register(username, password)).thenThrow(new UsernameAlreadyTakenException(exceptionMessage));
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value(exceptionMessage));
    }

    @Test
    @DisplayName("/auth/login - невалидные данные для логина - возвращает 400")
    void shouldReturn400IfNotValidUsername() throws Exception {
        String username = "us";
        mockMvc.perform(post("/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\", \"password\":\"qwerty\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("/auth/login - валидные значения - возвращает токен")
    void shouldReturnToken() throws Exception {
        String username = "username";
        String password = "Qwe123#$%";
        String token = "token";
        when(authService.login(username, password)).thenReturn(token);
        mockMvc.perform(post("/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(token));
    }
}