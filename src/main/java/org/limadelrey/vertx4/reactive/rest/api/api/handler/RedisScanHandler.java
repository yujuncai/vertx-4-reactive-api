package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.R.Result;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.strategy.RedisContext;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.strategy.RedisOff4VersionStrategy;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.strategy.RedisOn4VersionStrategy;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.strategy.RedisStrategy;
import org.limadelrey.vertx4.reactive.rest.api.api.service.RedisService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.message.KeyVo;
import org.limadelrey.vertx4.reactive.rest.api.utils.RedisUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;
import org.limadelrey.vertx4.reactive.rest.api.verticle.RedisVerticle;

import java.util.List;

@Singleton
public class RedisScanHandler {
    private static final Logger LOGGER = LogManager.getLogger(RedisScanHandler.class);
    private final RedisService redisService= GuiceUtil.getGuice().getInstance(RedisService.class);
    private final RedisStrategy redisServiceOff4= GuiceUtil.getGuice().getInstance(RedisOff4VersionStrategy.class);
    private final RedisStrategy redisServiceOn4= GuiceUtil.getGuice().getInstance(RedisOn4VersionStrategy.class);
    public RedisScanHandler() {

    }

    private static final String ID_PARAMETER = "id";
    public Future<Response> scanRedis(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);
    // todo

        RedisAPI instance = RedisUtils.getInstance();
        Future serverVersion = redisService.getServerVersion(instance);
        Future dbSize = redisService.getDbSize(instance);
        serverVersion.onSuccess(s ->{
            dbSize.onSuccess(o ->{
                RedisContext context = null;
                LOGGER.info("redis version {}",s);
               if ( (Integer)s < 4) {
                    context = new RedisContext(redisServiceOff4, (Integer) o);
                } else {
                    context = new RedisContext(redisServiceOn4, (Integer) o);
                }
                Future future = context.contextInterface();
                future.onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<>().ok(success)))
                        .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, (Throwable) throwable));
            });
        });
        return   null;
    }





    public Future<Response> setRedis(RoutingContext rc){
        RedisAPI instance = RedisUtils.getInstance();
        instance.set(List.of("mykey", "myvalue"), res -> {
            if (res.succeeded()) {
                System.out.println("Key stored");
            } else {
                System.out.println("Failed to store key");
            }
        });
        instance.sadd(  List.of("set","set1","set2","set3"));
        instance.zadd( List.of("zset","1","zset1","2","zset2"));

        for (int i = 0; i <20000 ; i++) {
            String key= "key4"+i;
                    String value="value4444444"+i;
                    instance.set(List.of(key, value), res -> {
                        if (res.succeeded()) {
                           // System.out.println("Key stored");
                        } else {
                            System.out.println(res.cause());
                        }
                    });
        }

        Future<Response> hset = instance.hset(List.of("hset", "hset1", "hset1"));

        return  hset.onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result()))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }


    public Future<List<KeyVo>> getRedis(RoutingContext rc){

        Future<List<KeyVo>> redisTopN = RedisVerticle.getRedisTopN();

        return  redisTopN.onSuccess(success -> {
            ResponseUtils.buildOkResponse(rc, new Result().ok(success));
            RedisVerticle.clearRedisTopN();
        })
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));

    }



}
