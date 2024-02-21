package org.limadelrey.vertx4.reactive.rest.api.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import io.vertx.ext.web.templ.rocker.RockerTemplateEngine;
import io.vertx.mysqlclient.MySQLPool;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookValidationHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.ErrorHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.repository.BookRepository;
import org.limadelrey.vertx4.reactive.rest.api.api.router.BookRouter;
import org.limadelrey.vertx4.reactive.rest.api.api.router.HealthCheckRouter;
import org.limadelrey.vertx4.reactive.rest.api.api.router.MetricsRouter;
import org.limadelrey.vertx4.reactive.rest.api.api.router.TemplatesRouter;
import org.limadelrey.vertx4.reactive.rest.api.api.service.BookService;
import org.limadelrey.vertx4.reactive.rest.api.utils.ConfigUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.DbUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class PagesVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(PagesVerticle.class);
    private static final String HTTP_PAGES_PORT = "http.pages.port";
    @Override
    public void start(Promise<Void> promise) {

        final TemplatesRouter templatesRouter = new TemplatesRouter(vertx);

        final Router router = Router.router(vertx);
        router.route("/static/*").handler(StaticHandler.create());
        router.route().handler(TimeoutHandler.create(5000));
        ErrorHandler.buildHandler(router);
        templatesRouter.setRouter(router);

        router.route("/templates/v1/*").handler(TemplateHandler.create(RockerTemplateEngine.create()));


        buildHttpServer(vertx, promise, router);
    }

    /**
     * Run HTTP pages server on port 8889 with specified routes
     *
     * @param vertx   Vertx context
     * @param promise Callback
     * @param router  Router
     */
    private void buildHttpServer(Vertx vertx,
                                 Promise<Void> promise,
                                 Router router) {

        final Properties properties = ConfigUtils.getInstance().getProperties();
        final   Integer port=  Integer.parseInt(properties.getProperty(HTTP_PAGES_PORT));
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, http -> {
                    if (http.succeeded()) {
                        promise.complete();
                        LOGGER.info(LogUtils.RUN_HTTP_PAGES_SERVER_SUCCESS_MESSAGE.buildMessage(port));
                    } else {
                        promise.fail(http.cause());
                        LOGGER.info(LogUtils.RUN_HTTP_PAGES_SERVER_ERROR_MESSAGE.buildMessage());
                    }
                });
    }

}
