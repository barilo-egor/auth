package tgb.cryptoexchange.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tgb.cryptoexchange.auth.entity.User;
import tgb.cryptoexchange.auth.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("loadUserByUsername(username) - пользователя нет в БД - проброс UsernameNotFoundException")
    void shouldThrowExceptionWhenUsernameNotFound() {
        String username = "test";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username), "User not found");
    }

    @Test
    @DisplayName("loadUserByUsername(username) - пользователь есть в БД - возвращение пользователя с данным юзернеймом")
    void shouldReturnUser() {
        String username = "test";
        String password = "password";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(password);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        UserDetails actual = userService.loadUserByUsername(username);
        assertAll(
                () -> assertEquals(user.getUsername(), actual.getUsername()),
                () -> assertEquals(user.getPassword(), actual.getPassword()),
                () -> assertEquals(0, actual.getAuthorities().size())
        );
    }

    @Test
    @DisplayName("getUsernames() - в БД нет пользователей - возвращает пустой список")
    void shouldReturnEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        List<String> actual = userService.getUsernames();
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("getUsernames() - в БД есть пользователи - возвращает список юзернеймов")
    void shouldReturnUsernames() {
        List<User> users = new ArrayList<>();
        users.add(new User(1L, "test1", "password1"));
        users.add(new User(2L, "test2", "password2"));
        users.add(new User(3L, "test3", "password3"));
        when(userRepository.findAll()).thenReturn(users);
        List<String> actual = userService.getUsernames();
        assertAll(
                () -> assertNotNull(actual),
                () -> assertEquals(3, actual.size()),
                () -> assertTrue(actual.contains("test1")),
                () -> assertTrue(actual.contains("test2")),
                () -> assertTrue(actual.contains("test3"))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @NullSource
    @DisplayName("delete(username) - пользователь не найден - проброс UsernameNotFoundException")
    void shouldThrowExceptionWhenUserNotFound(String username) {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.delete(username), "User not found");
    }

    @ParameterizedTest
    @ValueSource(strings = {"qwe", "username"})
    @DisplayName("delete(username) - пользователь найден - пользователь удален")
    void shouldDeleteUser(String username) {
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        userService.delete(username);
        verify(userRepository).delete(user);
    }
}