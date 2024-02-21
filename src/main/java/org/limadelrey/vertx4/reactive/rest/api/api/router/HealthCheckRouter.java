package org.limadelrey.vertx4.reactive.rest.api.api.router;

import io.vertx.core.Vertx;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgPool;
import org.limadelrey.vertx4.reactive.rest.api.utils.DbUtils;

public class HealthCheckRouter {

    private HealthCheckRouter() {

    }

    /**
     * Set health check routes
     *
     * @param router   Router
     */
    public static void setRouter(Router router) {
        Vertx vertx=Vertx.currentContext().owner();
        final HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);
        PgPool dbClient = DbUtils.getInstance();
        healthCheckHandler.register("database",
                promise ->
                        dbClient.getConnection(connection -> {
                            if (connection.failed()) {
                                promise.fail(connection.cause());
                            } else {
                                connection.result().close();
                                promise.complete(Status.OK());
                            }
                        })
        );

        router.get("/health").handler(healthCheckHandler);
    }

}
