package org.limadelrey.vertx4.reactive.rest.api.utils;

import com.google.inject.Singleton;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import java.util.List;
import java.util.Properties;

public class RedisUtils {
    private static final Logger LOGGER = LogManager.getLogger(RedisUtils.class);
    public static final String REDIS_URL = "redis.url";
    public static final String REDIS_PASSWORD = "redis.password";

    public RedisUtils() {

    }
    private static final RedisUtils instance = new RedisUtils();
    private volatile  static RedisAPI redis ;
    public static RedisAPI getInstance() {

        if (redis == null) {
            synchronized (RedisUtils.class) {
                if (redis == null) {
                    LOGGER.info("初始化redis");
                    redis = instance.buildRedisClient();
                }
            }
        }
        return redis;
    }






    @Singleton
    public  RedisAPI buildRedisClient() {
        final Properties properties = ConfigUtils.getInstance().getProperties();
        RedisOptions options = new RedisOptions()
                .setConnectionString(properties.getProperty(REDIS_URL)+"/0")
                .setMaxPoolSize(1000).setMaxPoolWaiting(2000000).setMaxWaitingHandlers(100000);


        if(StringUtils.isNotEmpty(properties.getProperty(REDIS_PASSWORD))){
            options.setPassword(properties.getProperty(REDIS_PASSWORD));
        }


        Vertx vertx = Vertx.currentContext().owner();
        Redis redis = Redis.createClient(vertx, options);

        RedisAPI redisAPI = RedisAPI.api(redis);

        return redisAPI;
    }





}
