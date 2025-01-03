package org.limadelrey.vertx4.reactive.rest.api.api.router;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.vertx.core.TimeoutStream;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.impl.JsonUtil;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.R.Result;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookValidationHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.JwtAuthHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.PushHandler;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;
import org.limadelrey.vertx4.reactive.rest.api.verticle.PreVerticle;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BookRouter {
    private static final Logger LOGGER = LogManager.getLogger(BookRouter.class);
    private final Vertx vertx=Vertx.currentContext().owner();
    private final BookHandler bookHandler= GuiceUtil.getGuice().getInstance(BookHandler.class);

    private final BookValidationHandler bookValidationHandler=GuiceUtil.getGuice().getInstance(BookValidationHandler.class);

    private final JwtAuthHandler jwtHandler= GuiceUtil.getGuice().getInstance(JwtAuthHandler.class);
    public static  final  String CONSUMER_ADDRESS="com.end";

    public BookRouter() {

    }

    /**
     * Set books API routes
     *
     * @param router Router
     */
    public void setRouter(Router router) {
        router.mountSubRouter("/api/v1", buildBookRouter());
    }

    /**
     * Build books API
     * All routes are composed by an error handler, a validation handler and the actual business logic handler
     */
    private Router buildBookRouter() {
        final Router bookRouter = Router.router(vertx);

        bookRouter.route("/books*")
                .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
                .handler(BodyHandler.create().setBodyLimit(400000).setDeleteUploadedFilesOnEnd(true).setHandleFileUploads(true));
        bookRouter.get("/books").handler(bookValidationHandler.readAll()).handler(jwtHandler::TokenAuth).handler(bookHandler::readAll);
        bookRouter.get("/books/:id").handler(bookValidationHandler.readOne()).handler(jwtHandler::TokenAuth).handler(bookHandler::readOne);
        bookRouter.post("/books").handler(bookValidationHandler.create()).handler(jwtHandler::TokenAuth).handler(bookHandler::create);
        bookRouter.put("/books/:id").handler(bookValidationHandler.update()).handler(jwtHandler::TokenAuth).handler(bookHandler::update);
        bookRouter.delete("/books/:id").handler(bookValidationHandler.delete()).handler(jwtHandler::TokenAuth).handler(bookHandler::delete);

/*
        bookRouter.get("/sse") .handler(LoggerHandler.create(LoggerFormat.DEFAULT)).handler(routingContext ->{
            HttpServerResponse response = routingContext.response();
            // 使用标志位确保响应头只设置一次
            response
                        .putHeader("content-type", "text/event-stream")
                        .putHeader("cache-control", "no-cache")
                        .putHeader("connection", "keep-alive")
                        .setChunked(true); // 启用分块传输编码

            response.end("1");


            routingContext.addEndHandler(v -> {
                System.out.println("Response end.");
            });


        });
*/


        return bookRouter;
    }

}
