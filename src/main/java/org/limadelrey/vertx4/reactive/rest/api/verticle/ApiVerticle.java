package org.limadelrey.vertx4.reactive.rest.api.verticle;

import io.netty.handler.codec.spdy.SpdySessionHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.ErrorHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.router.BookRouter;
import org.limadelrey.vertx4.reactive.rest.api.api.router.HealthCheckRouter;
import org.limadelrey.vertx4.reactive.rest.api.api.router.MetricsRouter;
import org.limadelrey.vertx4.reactive.rest.api.api.router.PushRouter;
import org.limadelrey.vertx4.reactive.rest.api.utils.ConfigUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;

import java.util.Properties;

public class ApiVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LogManager.getLogger(ApiVerticle.class);
    private static final String HTTP_PORT = "http.port";
    @Override
    public void start(Promise<Void> promise) {





        final BookRouter bookRouter = new BookRouter();
        final PushRouter pushRouter = new PushRouter();


        final Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.DELETE)
               );
        router.route().handler(TimeoutHandler.create(5000));







        ErrorHandler.buildHandler(router);
        HealthCheckRouter.setRouter( router);
        MetricsRouter.setRouter(router);

        bookRouter.setRouter(router);
        pushRouter.setRouter(router);
        router.get("/*").handler(rc -> {
            rc.response().setStatusCode(404).end("Custom 404 message");
        });
        buildHttpServer( promise, router);
    }

    /**
     * Run HTTP server on port 8888 with specified routes
     *
     * @param promise Callback
     * @param router  Router
     */
    private void buildHttpServer(
                                 Promise<Void> promise,
                                 Router router) {

        final Properties properties = ConfigUtils.getInstance().getProperties();
        final   int port=  Integer.parseInt(properties.getProperty(HTTP_PORT));
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, http -> {
                    if (http.succeeded()) {
                        promise.complete();
                        LOGGER.info(LogUtils.RUN_HTTP_SERVER_SUCCESS_MESSAGE.buildMessage(port));
                    } else {
                        promise.fail(http.cause());
                        LOGGER.info(LogUtils.RUN_HTTP_SERVER_ERROR_MESSAGE.buildMessage());
                    }
                });
    }

}
