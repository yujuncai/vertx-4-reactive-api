package org.limadelrey.vertx4.reactive.rest.api.api.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.JwtAuthHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.PushHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.PushValidationHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.RedisScanHandler;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;

public class RedisScanRouter {

    private final Vertx vertx=Vertx.currentContext().owner();

    private final RedisScanHandler redisScanHandler= GuiceUtil.getGuice().getInstance(RedisScanHandler.class);
    private final JwtAuthHandler jwtHandler= GuiceUtil.getGuice().getInstance(JwtAuthHandler.class);

    public RedisScanRouter() {

    }


    public void setRouter(Router router) {

        router.mountSubRouter("/scan/v1", buildPushRouter());
    }


    private Router buildPushRouter() {
        final Router scanRouter = Router.router(vertx);



        scanRouter.route("/*ToRedis")
                .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
                .handler(BodyHandler.create().setBodyLimit(1000).setDeleteUploadedFilesOnEnd(false).setHandleFileUploads(false));

        scanRouter.get("/scanToRedis/:id").handler(jwtHandler::TokenAuth).handler(redisScanHandler::scanRedis);

       // scanRouter.get("/setToRedis").handler(jwtHandler::TokenAuth).handler(redisScanHandler::setRedis);


        scanRouter.get("/getToRedis/:id").handler(jwtHandler::TokenAuth).handler(redisScanHandler::getRedis);
        return scanRouter;
    }

}
