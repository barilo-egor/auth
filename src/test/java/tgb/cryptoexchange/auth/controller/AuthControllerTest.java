package tgb.cryptoexchange.auth.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tgb.cryptoexchange.auth.config.AppSecurityProperties;
import tgb.cryptoexchange.auth.config.SecurityConfig;
import tgb.cryptoexchange.auth.controller.advice.GlobalExceptionHandler;
import tgb.cryptoexchange.auth.exception.LoginException;
import tgb.cryptoexchange.auth.exception.UsernameAlreadyTakenException;
import tgb.cryptoexchange.auth.service.AuthService;
import tgb.cryptoexchange.auth.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AuthControllerTest {

    @MockitoBean
    private AppSecurityProperties appSecurityProperties;

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
    @DisplayName("POST /auth/register - валидные значения - возвращает токен")
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
    @DisplayName("POST /auth/register - невалидное значение - возвращает 400")
    void shouldReturn400IfCredentialIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"username\", \"password\":\"qwerty\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login - проброшено LoginException - возвращает 403")
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
    @DisplayName("POST /auth/login - проброшено UsernameAlreadyTakenException - возвращает 403")
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
    @DisplayName("POST /auth/login - невалидные данные для логина - возвращает 403")
    void shouldReturn400IfNotValidUsername() throws Exception {
        String username = "us";
        mockMvc.perform(post("/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\", \"password\":\"qwerty\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /auth/login - валидные значения - возвращает токен")
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

    @Test
    @DisplayName("GET /auth/ - пользователи отсутствуют - возвращается ответ с пустым data")
    void shouldReturnEmptyList() throws Exception {
        when(userService.getUsernames()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/auth")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("GET /auth/ - пользователи отсутствуют - возвращается ответ с пустым data")
    void shouldReturnUsernamesList() throws Exception {
        List<String> usernames = new ArrayList<>();
        usernames.add("username1");
        usernames.add("username2");
        usernames.add("username3");
        when(userService.getUsernames()).thenReturn(usernames);
        mockMvc.perform(get("/auth")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("username1"))
                .andExpect(jsonPath("$.data[1]").value("username2"))
                .andExpect(jsonPath("$.data[2]").value("username3"))
                .andExpect(jsonPath("$.data[3]").doesNotExist());
    }

    @Test
    @DisplayName("DELETE /auth/ - пользователи отсутствуют - возвращается ответ с пустым data")
    void deleteShouldReturn400() throws Exception {
        doThrow(new UsernameNotFoundException("User not found")).when(userService).delete(anyString());
        mockMvc.perform(delete("/auth/someUsername")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.message").value("User not found"));
    }

    @Test
    @DisplayName("DELETE /auth/ - пользователь существует - возвращается 204, пользователь удален")
    void deleteShouldReturn204() throws Exception {
        mockMvc.perform(delete("/auth/someUsername")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /auth/ - пользователи отсутствуют - возвращается ответ с пустым data")
    void patchShouldReturn400IfUserNotFound() throws Exception {
        doThrow(new UsernameNotFoundException("User not found")).when(userService).updatePassword(anyString(), anyString());
        mockMvc.perform(patch("/auth/someUsername")
                        .param("password", "qweSQsd12312!@#")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.message").value("User not found"));
    }

    @Test
    @DisplayName("PATCH /auth/ - пользователи отсутствуют - возвращается ответ с пустым data")
    void patchShouldReturn400IfPasswordInvalid() throws Exception {
        doThrow(new UsernameNotFoundException("User not found")).when(userService).updatePassword(anyString(), anyString());
        mockMvc.perform(patch("/auth/someUsername")
                        .param("password", "newPassword")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.message").value("Invalid password"));
    }

    @Test
    @DisplayName("PATCH /auth - пользователь существует - возвращается 204, пароль обновлен")
    void patchShouldReturn204() throws Exception {
        mockMvc.perform(patch("/auth/someUsername")
                        .param("password", "qqweADSDS!@#352"))
                .andExpect(status().isNoContent());
    }

}