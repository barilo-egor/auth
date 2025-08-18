package tgb.cryptoexchange.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tgb.cryptoexchange.auth.entity.User;
import tgb.cryptoexchange.auth.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
}