package org.limadelrey.vertx4.reactive.rest.api.pages.router;

import cn.hutool.core.util.URLUtil;
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

import java.net.URL;
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

        router.get("/basic").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(s -> templatesHandler.basicPage(s)).handler(rc -> {
            // 渲染模板
            RockerOutput index=   templates.basic.template(rc.get("name")).render();
            rc.response().end( index.toString());
        });

        router.get("/index/:id").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(s -> templatesHandler.indexPage(s)).handler(rc -> {
         RockerOutput index=   templates.index.template(rc.get("title"),rc.get("name"),rc.get("path")).render();
         rc.response().end( index.toString());
        });

        router.get("/index").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(s -> templatesHandler.indexPage(s)).handler(rc -> {
            RockerOutput index=   templates.index.template(rc.get("title"),rc.get("name"),rc.get("path")).render();
            rc.response().end( index.toString());
        });


        router.get("/blog").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(s -> templatesHandler.indexPage(s)).handler(rc -> {
            // 渲染模板
            RockerOutput index=   templates.blog.template(rc.get("title")).render();
            rc.response().end( index.toString());
        });
        router.get("/blog-details/:id").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(s -> templatesHandler.indexPage(s)).handler(rc -> {
            // 渲染模板
            RockerOutput index=   templates.blog_details.template(rc.get("title")).render();
            rc.response().end( index.toString());
        });


        router.get("/portfolio-details/:id").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(s -> templatesHandler.indexPage(s)).handler(rc -> {
            // 渲染模板
            RockerOutput index=   templates.portfolio_details.template(rc.get("title")).render();
            rc.response().end( index.toString());
        });



        router.route("/*").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(rc -> {
            rc.response().setStatusCode(404).end("Custom 404 message");
        });

                        return router;
                    }
                }

