package org.limadelrey.vertx4.reactive.rest.api.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start() {
        final long start = System.currentTimeMillis();

        deployMigrationVerticle(vertx)
                .flatMap(x ->
                        deployApiVerticle(vertx)
                ).flatMap( x ->
                        deployTcpVerticle(vertx))
                .onSuccess(success -> LOGGER.info(LogUtils.RUN_APP_SUCCESSFULLY_MESSAGE.buildMessage(System.currentTimeMillis() - start)))
                .onFailure(throwable -> LOGGER.error(throwable.getMessage()));
    }

    private Future<Void> deployMigrationVerticle(Vertx vertx) {
        final DeploymentOptions options = new DeploymentOptions()
                .setWorker(true)
                .setWorkerPoolName("migrations-worker-pool")
                .setInstances(1)
                .setWorkerPoolSize(1);

        return vertx.deployVerticle(MigrationVerticle.class.getName(), options)
                .flatMap(vertx::undeploy);
    }

    private Future<String> deployApiVerticle(Vertx vertx) {
        return vertx.deployVerticle(ApiVerticle.class.getName(),new
                DeploymentOptions().setWorker(false).setInstances(1));

    }
    private Future<String> deployTcpVerticle(Vertx vertx) {
        return vertx.deployVerticle(TcpVerticle.class.getName(),new
                DeploymentOptions().setWorker(false).setInstances(1));

    }
}
