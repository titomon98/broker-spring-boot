package arqui.broker.services;

import arqui.broker.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String username, String rawPassword, String role);
    Optional<User> findByUsername(String username);
    boolean validatePassword(String rawPassword, String encodedPassword);
    List<User> findAll();
    boolean deleteUser(Integer id);
}