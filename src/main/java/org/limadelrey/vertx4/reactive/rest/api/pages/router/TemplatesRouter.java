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
import org.limadelrey.vertx4.reactive.rest.api.api.handler.JwtAuthHandler;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.pages.handler.TemplatesHandler;
import org.limadelrey.vertx4.reactive.rest.api.verticle.PagesVerticle;
import templates.index;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

public class TemplatesRouter {
    private final Vertx vertx;
    private TemplatesHandler templatesHandler;
    private final JwtAuthHandler jwtHandler= GuiceUtil.getGuice().getInstance(JwtAuthHandler.class);
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

        router.get("/login").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(s -> templatesHandler.basicPage(s)).handler(rc -> {
            // 渲染模板
            RockerOutput index=   templates.login.template(rc.get("host") ).render();
            rc.response().end( index.toString());
        });


        router.get("/register").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(s -> templatesHandler.basicPage(s)).handler(rc -> {
            RockerOutput index=   templates.register.template(rc.get("host") ).render();
            rc.response().end( index.toString());
        });


        router.get("/main").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(s -> templatesHandler.basicPage(s)).handler(jwtHandler::PageTokenAuth).handler(rc -> {
            RockerOutput index=   templates.main.template(rc.get("host")).render();
            rc.response().end( index.toString());
        });



        router.route("/*").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(rc -> {
            rc.response().setStatusCode(404).end("Custom 404 message");
        });

                        return router;
                    }
                }

