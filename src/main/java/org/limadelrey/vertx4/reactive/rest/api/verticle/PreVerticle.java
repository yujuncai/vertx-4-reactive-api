package org.limadelrey.vertx4.reactive.rest.api.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LogManager.getLogger(PreVerticle.class);

    @Override
    public void start(Promise<Void> promise) {

        SharedData sharedData = vertx.sharedData();
        sharedData.getAsyncMap("PgPool", result -> {
            if (result.succeeded()) {
                result.result().put("pool", "111111", putResult -> {
                    if (putResult.succeeded()) {
                        LOGGER.info("PgPool  shared successfully");
                    } else {
                        LOGGER.info("Failed to share PgPool");
                    }
                });
            } else {
                LOGGER.error("Failed to get async map");
            }

        });
        sharedData.getAsyncMap("PgPool", result -> {
            if (result.succeeded()) {
                AsyncMap<Object, Object> asyncMap = result.result();
                asyncMap.get("pool", getResult -> {
                    if (getResult.succeeded()) {
                        LOGGER.info("succeeded {}",getResult);
                    } else {
                        LOGGER.error("Failed to get PgPool from shared data");
                    }
                });
            } else {
                LOGGER.error("Failed to get async map");
            }
        });
    }


}






