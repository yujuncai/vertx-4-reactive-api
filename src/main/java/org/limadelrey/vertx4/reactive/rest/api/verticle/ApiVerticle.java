package org.limadelrey.vertx4.reactive.rest.api.verticle;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import io.vertx.mysqlclient.MySQLPool;

import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookValidationHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.ErrorHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.repository.BookRepository;
import org.limadelrey.vertx4.reactive.rest.api.api.router.BookRouter;
import org.limadelrey.vertx4.reactive.rest.api.api.router.HealthCheckRouter;
import org.limadelrey.vertx4.reactive.rest.api.api.router.MetricsRouter;
import org.limadelrey.vertx4.reactive.rest.api.api.service.BookService;
import org.limadelrey.vertx4.reactive.rest.api.utils.ConfigUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.DbUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ApiVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiVerticle.class);
    private static final String HTTP_PORT = "http.port";
    @Override
    public void start(Promise<Void> promise) {
        final MySQLPool dbClient = DbUtils.buildDbClient(vertx);
        final BookRepository bookRepository = new BookRepository();
        final BookService bookService = new BookService(dbClient, bookRepository);
        final BookHandler bookHandler = new BookHandler(bookService);
        final BookValidationHandler bookValidationHandler = new BookValidationHandler(vertx);
        final BookRouter bookRouter = new BookRouter(vertx, bookHandler, bookValidationHandler);

        final Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create()
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.DELETE)
               );
        router.route().handler(TimeoutHandler.create(5000));
        ErrorHandler.buildHandler(router);
        HealthCheckRouter.setRouter(vertx, router, dbClient);
        MetricsRouter.setRouter(router);
        bookRouter.setRouter(router);

        buildHttpServer(vertx, promise, router);
    }

    /**
     * Run HTTP server on port 8888 with specified routes
     *
     * @param vertx   Vertx context
     * @param promise Callback
     * @param router  Router
     */
    private void buildHttpServer(Vertx vertx,
                                 Promise<Void> promise,
                                 Router router) {

        final Properties properties = ConfigUtils.getInstance().getProperties();
        final   Integer port=  Integer.parseInt(properties.getProperty(HTTP_PORT));
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
