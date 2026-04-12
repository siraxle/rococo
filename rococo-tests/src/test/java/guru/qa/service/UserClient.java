package guru.qa.service;

import guru.qa.model.UserJson;
import java.util.List;

public interface UserClient {

    UserJson createUser(UserJson user);

    UserJson getUserById(String id);

    UserJson getUserByUsername(String username);

    List<UserJson> getAllUsers();

    UserJson updateUser(String id, String firstname, String lastname, String avatar);

    void deleteUser(String id);

    boolean existsById(String id);

    boolean existsByUsername(String username);
}