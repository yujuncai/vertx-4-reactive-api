package org.limadelrey.vertx4.reactive.rest.api.api.handler.strategy;

import io.vertx.core.Future;
import io.vertx.redis.client.RedisAPI;

import java.io.IOException;

public interface RedisStrategy {


    public Future handler(Integer dbSize, RedisAPI instance,String uuid) ;
}
