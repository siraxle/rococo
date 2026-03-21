package guru.qa.config;

import org.jetbrains.annotations.NotNull;

enum LocalConfig implements Config {
    INSTANCE;

    @Override
    public @NotNull String frontUrl() {
        return "http://localhost:3000/";
    }

    @Override
    public @NotNull String authUrl() {
        return "http://localhost:9000/";
    }

    @Override
    public @NotNull String gatewayUrl() {
        return "http://localhost:8081/";
    }

    @Override
    public @NotNull String userdataUrl() {
        return "http://localhost:8098/";
    }

    @Override
    public @NotNull String artistGrpcHost() {
        return "localhost";
    }

    @Override
    public int artistGrpcPort() {
        return 8091;
    }

    @Override
    public @NotNull String museumGrpcHost() {
        return "localhost";
    }

    @Override
    public int museumGrpcPort() {
        return 8092;
    }

    @Override
    public @NotNull String paintingGrpcHost() {
        return "localhost";
    }

    @Override
    public int paintingGrpcPort() {
        return 8093;
    }

    @Override
    public @NotNull String geoGrpcHost() {
        return "localhost";
    }

    @Override
    public int geoGrpcPort() {
        return 8095;
    }

    @Override
    public @NotNull String userdataGrpcHost() {
        return "localhost";
    }

    @Override
    public int userdataGrpcPort() {
        return 8096;
    }

    @Override
    public @NotNull String artistJdbcUrl() {
        return "jdbc:mysql://localhost:3306/rococo-artist";
    }

    @Override
    public @NotNull String museumJdbcUrl() {
        return "jdbc:mysql://localhost:3306/rococo-museum";
    }

    @Override
    public @NotNull String paintingJdbcUrl() {
        return "jdbc:mysql://localhost:3306/rococo-painting";
    }

    @Override
    public @NotNull String geoJdbcUrl() {
        return "jdbc:mysql://localhost:3306/rococo-geo";
    }

    @Override
    public @NotNull String userdataJdbcUrl() {
        return "jdbc:mysql://localhost:3306/rococo-userdata";
    }

    @Override
    public @NotNull String authJdbcUrl() {
        return "jdbc:mysql://localhost:3306/rococo-auth";
    }
}
