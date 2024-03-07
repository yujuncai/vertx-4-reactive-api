package org.limadelrey.vertx4.reactive.rest.api.api.router;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.*;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.JwtUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class PushRouter {

    private final Vertx vertx=Vertx.currentContext().owner();


    private final PushHandler pushHandler= GuiceUtil.getGuice().getInstance(PushHandler.class);

    private final JwtAuthHandler jwtHandler= GuiceUtil.getGuice().getInstance(JwtAuthHandler.class);
    private final PushValidationHandler pushValidationHandler=GuiceUtil.getGuice().getInstance(PushValidationHandler.class);
    public PushRouter() {

    }


    public void setRouter(Router router) {

        router.mountSubRouter("/push/v1", buildPushRouter());
    }


    private Router buildPushRouter() {
        final Router pushRouter = Router.router(vertx);



        pushRouter.route("/pushTo*")
                .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
                .handler(BodyHandler.create().setBodyLimit(1000).setDeleteUploadedFilesOnEnd(false).setHandleFileUploads(false));

        pushRouter.get("/pushToWeChat").handler(pushValidationHandler.templateMessage1()).handler(jwtHandler::TokenAuth).handler(pushHandler::pushToWeChat);


        return pushRouter;
    }

}
