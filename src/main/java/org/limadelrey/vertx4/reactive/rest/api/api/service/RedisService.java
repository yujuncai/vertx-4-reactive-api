package org.limadelrey.vertx4.reactive.rest.api.api.service;

import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.impl.types.BulkType;
import io.vertx.redis.client.impl.types.MultiType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Singleton
public class RedisService {

    private static final Logger LOGGER = LogManager.getLogger(RedisService.class);


    public Future getDbSize(RedisAPI instance) {
        Future config = instance.config(List.of("get", "databases"));
        return  config.map(m ->{
            MultiType type=  (MultiType)m;
            LOGGER.info("获取当前链接的DB数量 {}",   type.get(1));
            return type.get(1).toInteger();
        });
    }


    public Future getServerVersion(RedisAPI instance) {
        Future version = instance.info(List.of("Server"));

        return  version.map(m ->{
            BulkType result = (BulkType) m;
            String string = result.toString();
            int redisVersion = string.indexOf("redis_version");
            String substring = string.substring(redisVersion + 14, redisVersion + 15);
            LOGGER.info("获取当前链接的版本 {}", substring);
            return  Integer.valueOf(substring);
        });





    }


}