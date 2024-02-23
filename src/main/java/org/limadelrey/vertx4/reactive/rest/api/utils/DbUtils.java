package org.limadelrey.vertx4.reactive.rest.api.utils;

import com.google.inject.Singleton;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class DbUtils {
    private static final Logger LOGGER = LogManager.getLogger(DbUtils.class);
    private static final String HOST_CONFIG = "datasource.host";
    private static final String PORT_CONFIG = "datasource.port";
    private static final String DATABASE_CONFIG = "datasource.database";
    private static final String USERNAME_CONFIG = "datasource.username";
    private static final String PASSWORD_CONFIG = "datasource.password";

    public DbUtils() {

    }
    private static final DbUtils instance = new DbUtils();
    private volatile  static PgPool pool ;
    public static PgPool getInstance() {

        if (pool == null) {
            synchronized (DbUtils.class) {
                if (pool == null) {
                    LOGGER.info("初始化连接池");
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
    public  PgPool buildDbClient() {
        final Properties properties = ConfigUtils.getInstance().getProperties();

        Vertx vertx = Vertx.currentContext().owner();


        final PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(Integer.parseInt(properties.getProperty(PORT_CONFIG)))
                .setHost(properties.getProperty(HOST_CONFIG))
                .setDatabase(properties.getProperty(DATABASE_CONFIG))
                .setUser(properties.getProperty(USERNAME_CONFIG))
                .setPassword(properties.getProperty(PASSWORD_CONFIG))
                .setReconnectAttempts(10)
                .setReconnectInterval(1000);


        final PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(50).setShared(true)
                .setName("DB-pool")
                .setEventLoopSize(Runtime.getRuntime().availableProcessors());


        Pool build = PgBuilder
                .pool()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .using(vertx)
                .build();
        return (PgPool) build;
    }



  /*  @Singleton
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
    }*/

    /**
     * Build Flyway configuration that is used to run migrations
     *
     * @return Flyway configuration
     */
    public static Configuration buildMigrationsConfiguration() {
        final Properties properties = ConfigUtils.getInstance().getProperties();

      //  final String url = "jdbc:mysql://" + properties.getProperty(HOST_CONFIG) + ":" + properties.getProperty(PORT_CONFIG) + "/" + properties.getProperty(DATABASE_CONFIG)+"?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";
        final String url = "jdbc:postgresql://" + properties.getProperty(HOST_CONFIG) + ":" + properties.getProperty(PORT_CONFIG) + "/" + properties.getProperty(DATABASE_CONFIG);
        return new FluentConfiguration().dataSource(url, properties.getProperty(USERNAME_CONFIG), properties.getProperty(PASSWORD_CONFIG));
    }

}
