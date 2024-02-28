package org.limadelrey.vertx4.reactive.rest.api.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import io.vertx.ext.web.templ.rocker.RockerTemplateEngine;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.ErrorHandler;
import org.limadelrey.vertx4.reactive.rest.api.pages.handler.TemplatesHandler;
import org.limadelrey.vertx4.reactive.rest.api.pages.router.TemplatesRouter;
import org.limadelrey.vertx4.reactive.rest.api.utils.ConfigUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class PagesVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LogManager.getLogger(PagesVerticle.class);
    private static final String HTTP_PAGES_PORT = "http.pages.port";

    public static final String PAGES_PATH = "/pages/v1";


    @Override
    public void start(Promise<Void> promise) {


        final TemplatesHandler templatesHandler = new TemplatesHandler();
        final TemplatesRouter templatesRouter = new TemplatesRouter(vertx,templatesHandler);

        final Router router = Router.router(vertx);
        router.route("/static/*").handler(StaticHandler.create());
        router.route().handler(TimeoutHandler.create(5000));
        ErrorHandler.buildHandler(router);
        templatesRouter.setRouter(router);
        router.route(PAGES_PATH.concat("/*")).handler(TemplateHandler.create(RockerTemplateEngine.create()));

        buildHttpServer( promise, router);
    }

    /**
     * Run HTTP pages server on port 8889 with specified routes
     *
     * @param promise Callback
     * @param router  Router
     */
    private void buildHttpServer(
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
