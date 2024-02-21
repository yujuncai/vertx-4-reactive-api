package org.limadelrey.vertx4.reactive.rest.api.api.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.rocker.RockerTemplateEngine;
import io.vertx.micrometer.PrometheusScrapingHandler;

public class TemplatesRouter {
    private final Vertx vertx;
    public TemplatesRouter(Vertx vertx) {
        this.vertx = vertx;
    }

    /**
     * Set metrics routes
     *
     * @param router Router
     */
    public  void setRouter(Router router) {
        router.mountSubRouter("/templates/v1", buildTemplateRouter());


}

    private Router buildTemplateRouter() {
        final Router router = Router.router(vertx);
        router.route("/index").handler(ctx -> {
            ctx.put("title", "Vert.x Web Example Using Rocker");
            ctx.put("name", "Rocker");
            ctx.put("path", ctx.request().path());
            ctx.next();
        });

        router.route("/main").handler(ctx -> {
            ctx.put("title", "Vert.x Web Example Using Rocker");
            ctx.put("content", "Rocker");
            ctx.next();
        });
        return router;
    }




}
