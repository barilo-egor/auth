package tgb.cryptoexchange.auth.dto;

import lombok.Data;

@Data
public class UserCredentialsDTO {

    private String username;

    private String password;

    public boolean isValidForRegistration() {
        return username != null && password != null && !username.isBlank() && !password.isBlank()
                && password.length() >= 8 && username.length() >= 3
                && password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).+$");
    }

    public boolean isValidForLogin() {
        return username != null && password != null
                && !username.isBlank() && !password.isBlank()
                && username.length() >= 3;
    }
}
