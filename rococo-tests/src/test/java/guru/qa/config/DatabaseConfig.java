package guru.qa.config;

import com.p6spy.engine.spy.P6DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = "guru.qa.service.db")
public class DatabaseConfig {

    private final Config config = Config.getInstance();

    private DataSource createDataSource(String jdbcUrl, String poolName) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(config.jdbcUser());
        hikariConfig.setPassword(config.jdbcPassword());
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setPoolName(poolName);
//        hikariConfig.setConnectionTestQuery("SELECT 1");
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        return new P6DataSource(hikariDataSource);
    }

    @Bean
    public DataSource artistDataSource() {
        return createDataSource(config.artistJdbcUrl(), "ArtistPool");
    }

    @Bean
    public DataSource museumDataSource() {
        return createDataSource(config.museumJdbcUrl(), "MuseumPool");
    }

    @Bean
    public DataSource paintingDataSource() {
        return createDataSource(config.paintingJdbcUrl(), "PaintingPool");
    }

    @Bean
    public DataSource userdataDataSource() {
        return createDataSource(config.userdataJdbcUrl(), "UserdataPool");
    }

    @Bean
    public DataSource geoDataSource() {
        return createDataSource(config.geoJdbcUrl(), "GeoPool");
    }

    @Bean
    public DataSource authDataSource() {
        return createDataSource(config.authJdbcUrl(), "AuthPool");
    }

    @Bean
    public JdbcTemplate artistJdbcTemplate(@Qualifier("artistDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate museumJdbcTemplate(@Qualifier("museumDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate paintingJdbcTemplate(@Qualifier("paintingDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate userdataJdbcTemplate(@Qualifier("userdataDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate geoJdbcTemplate(@Qualifier("geoDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate authJdbcTemplate(@Qualifier("authDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}