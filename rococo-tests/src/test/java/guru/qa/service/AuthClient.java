package guru.qa.service;

import java.io.IOException;

public interface AuthClient {

    void createUser(String username, String password);

    void deleteUser(String username);

    boolean userExists(String username);

    String getUserPassword(String username);

    int getUserAuthoritiesCount(String username);

    void registerUser(String username, String password) throws IOException, InterruptedException;

    String login(String username, String password) throws IOException;

    String registerAndLogin(String username, String password) throws IOException, InterruptedException;
}