package ru.itis.studyhelper.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class DatabaseUtil {
    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = loadProperties();
            HikariConfig config = new HikariConfig();

            log.info("set url");
            config.setJdbcUrl(props.getProperty("db.url"));
            log.info("set username");
            config.setUsername(props.getProperty("db.username"));
            log.info("set password");
            config.setPassword(props.getProperty("db.password"));
            log.info("set driver class name");
            config.setDriverClassName(props.getProperty("db.driver"));

            config.setMaximumPoolSize(Integer.parseInt(
                    props.getProperty("db.pool.maxPoolSize", "10")
            ));
            config.setMinimumIdle(Integer.parseInt(
                    props.getProperty("db.pool.minIdle", "2")));
            config.setConnectionTimeout(Long.parseLong(
                    props.getProperty("db.pool.connectionTimeout", "30000")));

            config.setConnectionTestQuery("SELECT 1");

            dataSource = new HikariDataSource(config);
            log.info("connection pool успешно инициализирован");

        } catch (Exception e) {
            log.info("ошибка инициализации connection pool");
            throw new RuntimeException();
        }
    }


    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();

        try (InputStream is = DatabaseUtil.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (is == null) {
                throw new IOException("Ошибка подключения");
            }

            props.load(is);
        }
        return props;
    }


    public static DataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("connection pool закрыт");
        }
    }
}
