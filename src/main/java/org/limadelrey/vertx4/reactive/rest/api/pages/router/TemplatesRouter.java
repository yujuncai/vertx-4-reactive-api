package org.limadelrey.vertx4.reactive.rest.api.pages.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import org.limadelrey.vertx4.reactive.rest.api.pages.handler.TemplatesHandler;
import org.limadelrey.vertx4.reactive.rest.api.verticle.PagesVerticle;

public class TemplatesRouter {
    private final Vertx vertx;
    private TemplatesHandler templatesHandler;
    public TemplatesRouter(Vertx vertx,TemplatesHandler templatesHandler) {

        this.templatesHandler = templatesHandler;
        this.vertx = vertx;
    }

    /**
     * Set metrics routes
     *
     * @param router Router
     */
    public  void setRouter(Router router) {
        router.mountSubRouter(PagesVerticle.PAGES_PATH, buildTemplateRouter());


}

    private Router buildTemplateRouter() {

        final Router router = Router.router(vertx);
        router.route(PagesVerticle.PAGES_PATH.concat("/*")).handler(LoggerHandler.create(LoggerFormat.DEFAULT));
        router.get("/index").handler(s -> templatesHandler.indexPage(s));
        router.get("/basic").handler(s -> templatesHandler.basicPage(s));
        return router;
    }




}
