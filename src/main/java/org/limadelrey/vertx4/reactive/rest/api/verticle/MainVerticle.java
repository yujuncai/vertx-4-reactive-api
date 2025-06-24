package org.limadelrey.vertx4.reactive.rest.api.verticle;

import io.vertx.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LogManager.getLogger(MainVerticle.class);

    @Override
    public void start() {
        final long start = System.currentTimeMillis();





                 deployApiVerticle(vertx)

                .flatMap(x -> deployDifyVerticle(vertx))
                .onSuccess(success -> LOGGER.info(LogUtils.RUN_APP_SUCCESSFULLY_MESSAGE.buildMessage(System.currentTimeMillis() - start)))
                .onFailure(throwable -> LOGGER.error(throwable.getMessage()));

    }


    private Future<String> deployApiVerticle(Vertx vertx) {
        return vertx.deployVerticle(ApiVerticle.class.getName(),
                new DeploymentOptions()
                        .setInstances(Runtime.getRuntime().availableProcessors()/4 == 0 ?1 : Runtime.getRuntime().availableProcessors()/4).setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
        );


    }

    private Future<String> deployDifyVerticle(Vertx vertx) {
        return vertx.deployVerticle(DifyVerticle.class.getName(),
                new DeploymentOptions()
                        .setInstances(Runtime.getRuntime().availableProcessors()/4 == 0 ?1 : Runtime.getRuntime().availableProcessors()/4).setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
        );


    }




}
