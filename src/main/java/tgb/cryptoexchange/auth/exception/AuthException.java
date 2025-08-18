package tgb.cryptoexchange.auth.exception;

/**
 * Исключение сервиса авторизации
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
