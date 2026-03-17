package io.student.rococo.service;

import io.student.rococo.data.Authority;
import io.student.rococo.data.AuthorityEntity;
import io.student.rococo.data.UserEntity;
import io.student.rococo.data.repository.UserRepository;
import io.student.rococo.grpc.UserdataGrpcClient;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserdataGrpcClient userdataGrpcClient;


    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserdataGrpcClient userdataGrpcClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userdataGrpcClient = userdataGrpcClient;
    }

    @Transactional
    public @Nonnull
    String registerUser(@Nonnull String username, @Nonnull String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEnabled(true);
        userEntity.setAccountNonExpired(true);
        userEntity.setCredentialsNonExpired(true);
        userEntity.setAccountNonLocked(true);
        userEntity.setUsername(username);
        userEntity.setPassword(passwordEncoder.encode(password));

        AuthorityEntity readAuthorityEntity = new AuthorityEntity();
        readAuthorityEntity.setAuthority(Authority.read);
        AuthorityEntity writeAuthorityEntity = new AuthorityEntity();
        writeAuthorityEntity.setAuthority(Authority.write);

        userEntity.addAuthorities(readAuthorityEntity, writeAuthorityEntity);

        UserEntity savedUser = userRepository.save(userEntity);

        try {
            userdataGrpcClient.createUser(savedUser.getUsername());
            LOG.info("User profile created in userdata-service for: {}", username);
        } catch (Exception e) {
            LOG.error("Failed to create user profile in userdata-service for: {}", username, e);
        }

        return savedUser.getUsername();
    }
}
