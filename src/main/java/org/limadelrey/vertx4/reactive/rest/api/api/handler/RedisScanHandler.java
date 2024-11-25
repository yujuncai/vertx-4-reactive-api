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
import org.limadelrey.vertx4.reactive.rest.api.api.model.BookGetByIdResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.service.BookService;
import org.limadelrey.vertx4.reactive.rest.api.api.service.RedisService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.message.KeyVo;
import org.limadelrey.vertx4.reactive.rest.api.utils.RedisUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;
import org.limadelrey.vertx4.reactive.rest.api.verticle.RedisVerticle;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Singleton
public class RedisScanHandler {
    private static final Logger LOGGER = LogManager.getLogger(RedisScanHandler.class);
    private final RedisService redisService= GuiceUtil.getGuice().getInstance(RedisService.class);
    private final RedisStrategy redisServiceOff4= GuiceUtil.getGuice().getInstance(RedisOff4VersionStrategy.class);
    private final RedisStrategy redisServiceOn4= GuiceUtil.getGuice().getInstance(RedisOn4VersionStrategy.class);
    private final BookService bookService= GuiceUtil.getGuice().getInstance(BookService.class);
    public RedisScanHandler() {

    }

    private static final String ID_PARAMETER = "id";
    public Future<Response> scanRedis(RoutingContext rc)  {
        final String id = rc.pathParam(ID_PARAMETER);

        Future<BookGetByIdResponse> bookGetByIdResponseFuture = bookService.readOne(Integer.parseInt(id));
        Future<RedisAPI> FutureApi =  RedisUtils.getDymicInstance(bookGetByIdResponseFuture);
        FutureApi.onSuccess(instance ->{
            Future serverVersion = redisService.getServerVersion(instance);
            Future dbSize = redisService.getDbSize(instance);
            serverVersion.onSuccess(s ->{
                dbSize.onSuccess(o ->{
                    RedisContext context = null;
                    LOGGER.info("redis version {}",s);
                    if ( (Integer)s < 4) {
                        context = new RedisContext(redisServiceOff4, (Integer) o,instance);
                    } else {
                        context = new RedisContext(redisServiceOn4, (Integer) o,instance);
                    }
                    Future future = context.contextInterface();
                    future.onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<>().ok(success)))
                            .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, (Throwable) throwable));
                });
            });
        });



        return   null;
    }








    public Future<List<KeyVo>> getRedis(RoutingContext rc){
        final String pingId = rc.pathParam(ID_PARAMETER);
        Future<List<KeyVo>> redisTopN = RedisVerticle.getRedisTopN(pingId);

        return  redisTopN.onSuccess(success -> {
            ResponseUtils.buildOkResponse(rc, new Result().ok(success));
            RedisVerticle.clearRedisTopN(pingId);
        })
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));

    }



}
