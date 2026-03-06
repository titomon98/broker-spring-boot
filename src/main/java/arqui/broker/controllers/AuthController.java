package arqui.broker.controllers;

import arqui.broker.config.JwtUtil;
import arqui.broker.models.User;
import arqui.broker.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            String role = body.getOrDefault("role", "ROLE_USER");

            User created = userService.createUser(username, password, role);

            return ResponseEntity.status(201).body(Map.of(
                    "message", "Usuario creado exitosamente",
                    "username", created.getUsername(),
                    "role", created.getRole()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        return userService.findByUsername(username)
                .filter(user -> userService.validatePassword(password, user.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getUsername());
                    return ResponseEntity.ok(Map.of(
                            "token", token,
                            "username", user.getUsername(),
                            "role", user.getRole()
                    ));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas")));
    }
}