package tgb.cryptoexchange.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserCredentialsDTOTest {

    @ParameterizedTest
    @CsvSource({
            "admin,123Qwe!@#",
            "user,saD123^%#$",
            "adm,qw125432^#QWE"
    })
    @DisplayName("isValidForRegistration() - валидные значения - true")
    void isValidForRegistrationShouldReturnTrueIfCredentialsIsValid(String username, String password) {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername(username);
        userCredentialsDTO.setPassword(password);
        assertTrue(userCredentialsDTO.isValidForRegistration());
    }

    @ParameterizedTest
    @CsvSource({
            "admin,123Qwe!@#",
            "user,saD123^%#$",
            "adm,qw125432^#QWE"
    })
    @DisplayName("isValidForLogin() - валидные значения - true")
    void isValidForLoginShouldReturnTrueIfCredentialsIsValid(String username, String password) {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername(username);
        userCredentialsDTO.setPassword(password);
        assertTrue(userCredentialsDTO.isValidForLogin());
    }

    @Test
    @DisplayName("isValidForRegistration() - null username - false")
    void isValidForRegistrationShouldReturnFalseIfUsernameIsNull() {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername(null);
        userCredentialsDTO.setPassword("qw125432^#QWE");
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
        userCredentialsDTO.setPassword("qw125432^#QWE");
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
        userCredentialsDTO.setPassword("qw125432^#QWE");
        assertFalse(userCredentialsDTO.isValidForLogin());
    }

    @Test
    @DisplayName("isValidForLogin() - null password] - false")
    void isValidForLoginShouldReturnFalseIfPasswordIsNull() {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername("username");
        userCredentialsDTO.setPassword(null);
        assertFalse(userCredentialsDTO.isValidForLogin());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "qw", "12", "1$", "q%", "1a", "q", "1", "%"})
    @DisplayName("isValidForLogin() - юзернейм пустой, либо длина меньше 3 - false")
    void isValidForLoginShouldReturnFalseIfUsernameIsBlankOrLength3(String username) {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername(username);
        userCredentialsDTO.setPassword("qw125432^#QWE");
        assertFalse(userCredentialsDTO.isValidForLogin());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    @DisplayName("isValidForLogin() - пустой password - false")
    void isValidForLoginShouldReturnFalseIfPasswordIsBlank(String password) {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUsername("username");
        userCredentialsDTO.setPassword(password);
        assertFalse(userCredentialsDTO.isValidForLogin());
    }
}