package org.limadelrey.vertx4.reactive.rest.api.verticle;

import com.fizzed.rocker.runtime.RockerRuntime;
import io.vertx.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LogManager.getLogger(MainVerticle.class);

    @Override
    public void start() {
        final long start = System.currentTimeMillis();

            deployDbVerticle(vertx);
            deployMigrationVerticle(vertx)
                    .flatMap(x ->
                            deployPagesVerticle(vertx)
                    )
                    .flatMap(x ->
                            deployApiVerticle(vertx)
                    ).flatMap( x ->
                            deployTcpVerticle(vertx)
                    ).onSuccess(success -> LOGGER.info(LogUtils.RUN_APP_SUCCESSFULLY_MESSAGE.buildMessage(System.currentTimeMillis() - start)))
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
        return vertx.deployVerticle(ApiVerticle.class.getName(),
                new DeploymentOptions().setWorker(false)
                        .setInstances( Runtime.getRuntime().availableProcessors()).setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
                        );


    }
    private Future<String> deployTcpVerticle(Vertx vertx) {
        return vertx.deployVerticle(TcpVerticle.class.getName(),new
                DeploymentOptions().setWorker(false).setInstances(1));

    }


    private Future<String> deployDbVerticle(Vertx vertx) {
        return vertx.deployVerticle(PreVerticle.class.getName(),new
                DeploymentOptions().setWorker(false).setInstances(1));

    }


    private Future<String> deployPagesVerticle(Vertx vertx) {
        RockerRuntime.getInstance().setReloading(true);
        return vertx.deployVerticle(PagesVerticle.class.getName(),new
                DeploymentOptions().setWorker(false).setInstances(Runtime.getRuntime().availableProcessors()/2).setThreadingModel(ThreadingModel.VIRTUAL_THREAD));

    }

}
