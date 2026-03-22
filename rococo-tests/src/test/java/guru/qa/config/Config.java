package guru.qa.config;

import org.jetbrains.annotations.NotNull;

public interface Config {

    static Config getInstance() {
        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.INSTANCE
                : LocalConfig.INSTANCE;
    }

    @NotNull
    String frontUrl();

    @NotNull
    String authUrl();

    @NotNull
    String gatewayUrl();

    @NotNull
    String userdataUrl();

    @NotNull
    String artistGrpcHost();

    int artistGrpcPort();

    @NotNull
    String museumGrpcHost();

    int museumGrpcPort();

    @NotNull
    String paintingGrpcHost();

    int paintingGrpcPort();

    @NotNull
    String geoGrpcHost();

    int geoGrpcPort();

    @NotNull
    String userdataGrpcHost();

    int userdataGrpcPort();

    @NotNull
    String artistJdbcUrl();

    @NotNull
    String museumJdbcUrl();

    @NotNull
    String paintingJdbcUrl();

    @NotNull
    String geoJdbcUrl();

    @NotNull
    String userdataJdbcUrl();

    @NotNull
    String authJdbcUrl();

    default @NotNull String jdbcUser() {
        return "root";
    }

    default @NotNull String jdbcPassword() {
        return "secret";
    }
}