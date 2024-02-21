package org.limadelrey.vertx4.reactive.rest.api.utils;

import com.google.inject.Singleton;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import java.util.Properties;

public class DbUtils {

    private static final String HOST_CONFIG = "datasource.host";
    private static final String PORT_CONFIG = "datasource.port";
    private static final String DATABASE_CONFIG = "datasource.database";
    private static final String USERNAME_CONFIG = "datasource.username";
    private static final String PASSWORD_CONFIG = "datasource.password";

    public DbUtils() {

    }
    private static final DbUtils instance = new DbUtils();
    private volatile  static MySQLPool pool ;
    public static MySQLPool getInstance() {

        if (pool == null) {
            synchronized (DbUtils.class) {
                if (pool == null) {
                    pool = instance.buildDbClient();
                }
            }
        }
        return pool;
    }




    /**
     * Build DB client that is used to manage a pool of connections
     *
     *
     * @return PostgreSQL pool
     */
    @Singleton
    private   MySQLPool buildDbClient() {
        final Properties properties = ConfigUtils.getInstance().getProperties();
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(Integer.parseInt(properties.getProperty(PORT_CONFIG)))
                .setHost(properties.getProperty(HOST_CONFIG))
                .setDatabase(properties.getProperty(DATABASE_CONFIG))
                .setUser(properties.getProperty(USERNAME_CONFIG))
                .setPassword(properties.getProperty(PASSWORD_CONFIG));
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(15);
        Vertx owner = Vertx.currentContext().owner();
        MySQLPool client = MySQLPool.pool(owner, connectOptions, poolOptions);
        return client;
    }

    /**
     * Build Flyway configuration that is used to run migrations
     *
     * @return Flyway configuration
     */
    public static Configuration buildMigrationsConfiguration() {
        final Properties properties = ConfigUtils.getInstance().getProperties();
        //jdbc:mysql://localhost:3306/xxx?useUnicode=true&characterEncoding=utf8&useSSL=true";
        final String url = "jdbc:mysql://" + properties.getProperty(HOST_CONFIG) + ":" + properties.getProperty(PORT_CONFIG) + "/" + properties.getProperty(DATABASE_CONFIG)+"?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";

        return new FluentConfiguration().dataSource(url, properties.getProperty(USERNAME_CONFIG), properties.getProperty(PASSWORD_CONFIG));
    }

}
