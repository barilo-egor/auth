package tgb.cryptoexchange.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;

class UserCredentialsDTOTest {

    @Test
    @DisplayName("isValidForRegistration() - null username - false")
    void isValidForRegistrationShouldReturnFalseIfUsernameIsNull() {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername(null);
        userCredentialsDTO.setPassword("password");
        assertFalse(userCredentialsDTO.isValidForRegistration());
    }

    @Test
    @DisplayName("isValidForRegistration() - null password - false")
    void isValidForRegistrationShouldReturnFalseIfPasswordIsNull() {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername("username");
        userCredentialsDTO.setPassword(null);
        assertFalse(userCredentialsDTO.isValidForRegistration());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "ab", "1q", "q1", "!q", "-3", "32"})
    @DisplayName("isValidForRegistration() - длина юзернейма меньше 3, либо юзернейм пуст - false")
    void isValidForRegistrationShouldReturnFalseIfUsernameIsBlankOrLengthLessThan3(String username) {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername(username);
        userCredentialsDTO.setPassword("password");
        assertFalse(userCredentialsDTO.isValidForRegistration());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ",
            "qwerty", "qwe123", "qwerty%4", "1", "%",
            "qwertyqwerty", "123456789", "!@#$%^&*(", "qwerty12345", "qwerty!!@@##", "123456^%$#@",
            "Qwertyqwerty", "Qwerty12345", "!@#%QWERTY"
    })
    @DisplayName("isValidForRegistration() - пароль пуст, длина менее 8, не подходит под регулярное выражение - false")
    void isValidForRegistrationShouldReturnFalseIfPasswordIsBlankOrLengthLessThan8(String password) {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername("username");
        userCredentialsDTO.setPassword(password);
        assertFalse(userCredentialsDTO.isValidForRegistration());
    }

    @Test
    @DisplayName("isValidForLogin() - null username - false")
    void isValidForLoginShouldReturnFalseIfUsernameIsNull() {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername(null);
        userCredentialsDTO.setPassword("password");
        assertFalse(userCredentialsDTO.isValidForLogin());
    }

}