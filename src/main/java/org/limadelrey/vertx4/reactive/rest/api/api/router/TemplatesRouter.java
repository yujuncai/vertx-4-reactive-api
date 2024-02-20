package org.limadelrey.vertx4.reactive.rest.api.api.router;

import io.vertx.ext.web.Router;
import io.vertx.micrometer.PrometheusScrapingHandler;

public class TemplatesRouter {

    private TemplatesRouter() {

    }

    /**
     * Set metrics routes
     *
     * @param router Router
     */
    public static void setRouter(Router router) {

        router.route("/index").handler(ctx -> {
            System.out.println("Hello from Vert.x Web Example Using Rocker");
            ctx.put("title", "Vert.x Web Example Using Rocker");
            ctx.put("name", "Rocker");
            ctx.put("path", ctx.request().path());
            ctx.next();
        });

}}
