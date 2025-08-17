package tgb.cryptoexchange.auth.exception;

/**
 * Исключение сервиса авторизации
 */
public class AuthException extends RuntimeException {

    public AuthException() {
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
