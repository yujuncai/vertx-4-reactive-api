package org.limadelrey.vertx4.reactive.rest.api.api.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.*;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;

public class UserRouter {

    private final Vertx vertx=Vertx.currentContext().owner();



    private final UserValidationHandler userValidationHandler=GuiceUtil.getGuice().getInstance(UserValidationHandler.class);



    private final UserHandler userHandler=GuiceUtil.getGuice().getInstance(UserHandler.class);
    public UserRouter() {

    }


    public void setRouter(Router router) {

        router.mountSubRouter("/user/v1", buildPushRouter());
    }


    private Router buildPushRouter() {
        final Router pushRouter = Router.router(vertx);


        pushRouter.route("/userTo*")
                .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
                .handler(BodyHandler.create().setBodyLimit(1000).setDeleteUploadedFilesOnEnd(false).setHandleFileUploads(false));

        pushRouter.post("/userToLogin").handler(userValidationHandler.LoginMessage()).handler(userHandler::login);


        return pushRouter;
    }

}
