package tgb.cryptoexchange.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Модель данных создаваемого/существующего пользователя")
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDTO {

    @Schema(description = "Юзернейм пользователя.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Пароль пользователя.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(hidden = true)
    public boolean isValidForRegistration() {
        return username != null && password != null && !username.isBlank() && !password.isBlank()
                && password.length() >= 8 && username.length() >= 3
                && password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).+$");
    }

    @Schema(hidden = true)
    public boolean isValidForLogin() {
        return username != null && password != null
                && !username.isBlank() && !password.isBlank()
                && username.length() >= 3;
    }
}
