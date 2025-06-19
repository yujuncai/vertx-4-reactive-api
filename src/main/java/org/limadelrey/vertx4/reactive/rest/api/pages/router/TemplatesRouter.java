package org.limadelrey.vertx4.reactive.rest.api.pages.router;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fizzed.rocker.RockerOutput;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.JwtAuthHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.model.AgentInfosGetAllResponse;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.pages.handler.TemplatesHandler;
import org.limadelrey.vertx4.reactive.rest.api.verticle.PagesVerticle;


public class TemplatesRouter {
    private final Vertx vertx;
    private final TemplatesHandler templatesHandler;
    private final JwtAuthHandler jwtAuthHandler= GuiceUtil.getGuice().getInstance(JwtAuthHandler.class);
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



        router.get("/login").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(templatesHandler::indexPage).handler(rc -> {
            RockerOutput index=   templates.login.template("登录").render();
            rc.response().end( index.toString());
        });


        router.get("/index").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(templatesHandler::allAgents).handler(rc -> {
            AgentInfosGetAllResponse agents = rc.get("agents");
            JSONObject json = JSONUtil.parseObj(agents);
            RockerOutput index=   templates.index.template(json).render();
            rc.response().end( index.toString());
        });

        router.get("/chat").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(templatesHandler::allAgents).handler(rc -> {
          ;
            RockerOutput index=   templates.chat.template().render();
            rc.response().end( index.toString());
        });

     /*   router.route("/*").handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(rc -> {
            rc.response().setStatusCode(404).end("Custom 404 message");
        });*/

                        return router;
                    }
                }

