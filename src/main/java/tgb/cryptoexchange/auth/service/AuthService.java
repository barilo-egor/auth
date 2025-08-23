package tgb.cryptoexchange.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.auth.entity.User;
import tgb.cryptoexchange.auth.exception.LoginException;
import tgb.cryptoexchange.auth.exception.UsernameAlreadyTakenException;
import tgb.cryptoexchange.auth.repository.UserRepository;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String register(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyTakenException("Username is taken by another user");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
        log.info("Зарегистрирован новый пользователь {} под идентификатором {}", username, user.getId());
        return jwtService.generateToken(username);
    }

    public String login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new LoginException("Invalid credentials"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new LoginException("Invalid credentials");
        }
        log.debug("Была осуществлена аутентификация пользователя {} с идентификатором {}", username, user.getId());
        return jwtService.generateToken(username);
    }
}
