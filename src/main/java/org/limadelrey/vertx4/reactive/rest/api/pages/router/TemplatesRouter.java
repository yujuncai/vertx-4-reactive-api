package org.limadelrey.vertx4.reactive.rest.api.pages.router;

import com.fizzed.rocker.Rocker;
import com.fizzed.rocker.RockerOutput;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.rocker.RockerTemplateEngine;
import org.limadelrey.vertx4.reactive.rest.api.pages.handler.TemplatesHandler;
import org.limadelrey.vertx4.reactive.rest.api.verticle.PagesVerticle;
import templates.index;

import java.util.HashMap;
import java.util.List;

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




        router.get("/index/:id").handler(s -> templatesHandler.indexPage(s)).handler(rc -> {
            // 渲染模板

            System.out.println((String) rc.get("title"));
            System.out.println((String)rc.get("name"));
            System.out.println((String) rc.get("path"));

         RockerOutput index=   templates.index.template(rc.get("title"),rc.get("name"),rc.get("path")).render();
         rc.response().end( index.toString());
        });




        router.get("/basic").handler(s -> templatesHandler.basicPage(s));

        router.get("/*").handler(rc -> {
            rc.response().setStatusCode(404).end("Custom 404 message");
        });

                        return router;
                    }
                }

