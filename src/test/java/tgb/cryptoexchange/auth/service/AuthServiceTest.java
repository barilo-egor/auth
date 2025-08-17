package tgb.cryptoexchange.auth.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tgb.cryptoexchange.auth.entity.User;
import tgb.cryptoexchange.auth.exception.AuthException;
import tgb.cryptoexchange.auth.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Должен пробросить исключение, если юзернейм уже занят.")
    void registerShouldThrowExceptionWhenUserExists() {
        String username = "test";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword("password");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        assertThrows(
                AuthException.class,
                () -> authService.register(username, "password"),
                "Username is taken by another user"
        );
    }

    @Test
    @DisplayName("Должен зарегистрировать пользователя и вернуть токен.")
    void registerShouldSaveUserAndReturnToken() {
        String username = "test";
        String password = "password";
        String encodedPassword = "encodedPassword";
        String token = "token";

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(jwtService.generateToken(username)).thenReturn(token);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        String actual = authService.register(username, password);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(userCaptor.capture());
        User actualUser = userCaptor.getValue();
        assertAll(
                () -> assertEquals(username, actualUser.getUsername()),
                () -> assertEquals(encodedPassword, actualUser.getPassword()),
                () -> assertEquals(token, actual)
        );
    }

    @Test
    @DisplayName("Должен пробросить исключение, если пользователь по такому юзернейму не найден")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        String username = "test";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> authService.login(username, password),
                "User not found"
        );
    }


    @Test
    @DisplayName("Должен пробросить исключение, если пароль неверен.")
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        String username = "test";
        String password = "password";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);
        assertThrows(AuthException.class, () -> authService.login(username, password), "Invalid password");
    }

    @Test
    @DisplayName("Должен сгенерировать токен.")
    void shouldGenerateTokenWhenUserExists() {
        String username = "test";
        String password = "password";
        String encodedPassword = "encodedPassword";
        String token = "token";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(encodedPassword);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtService.generateToken(username)).thenReturn(token);
        String actual = authService.login(username, password);
        assertEquals(token, actual);
    }
}