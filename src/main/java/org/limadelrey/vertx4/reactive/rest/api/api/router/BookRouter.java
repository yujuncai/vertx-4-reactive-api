package org.limadelrey.vertx4.reactive.rest.api.api.router;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.vertx.core.Vertx;
import io.vertx.core.json.impl.JsonUtil;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookValidationHandler;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.verticle.PreVerticle;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class BookRouter {
    private static final Logger LOGGER = LogManager.getLogger(BookRouter.class);
    private final Vertx vertx=Vertx.currentContext().owner();
    private final BookHandler bookHandler= GuiceUtil.getGuice().getInstance(BookHandler.class);

    private final BookValidationHandler bookValidationHandler=GuiceUtil.getGuice().getInstance(BookValidationHandler.class);




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
                .handler(BodyHandler.create().setBodyLimit(40000000).setDeleteUploadedFilesOnEnd(true).setHandleFileUploads(true));
        bookRouter.get("/books").handler(bookValidationHandler.readAll()).handler(bookHandler::readAll);
        bookRouter.get("/books/:id").handler(bookValidationHandler.readOne()).handler(bookHandler::readOne);
        bookRouter.post("/books").handler(bookValidationHandler.create()).handler(bookHandler::create);
        bookRouter.put("/books/:id").handler(bookValidationHandler.update()).handler(bookHandler::update);
        bookRouter.delete("/books/:id").handler(bookValidationHandler.delete()).handler(bookHandler::delete);


        bookRouter.get("/push").handler(rc ->{

             String title = rc.queryParams().get("title");
             String content = rc.queryParams().get("content");

            final String templateId = "CTbywqtbXji9rVEkJMvSYG7IXUyy1XHtsEzJiB8pfVI";

            if(StrUtil.isBlank(title)||StrUtil.isBlank(content)){
                rc.response().setStatusCode(400).end("Custom 400 message");
                return;
            }

            SharedData sharedData = vertx.sharedData();
            sharedData.getAsyncMap("preMap", result -> {
                if (result.succeeded()) {
                    AsyncMap<Object, Object> asyncMap = result.result();
                    asyncMap.get("accessToken", getResult -> {
                        if (getResult.succeeded()) {
                            LOGGER.info("succeeded {}",getResult);

                            JSONObject data = new JSONObject();
                            data.put("touser", "ocsj86eIpwd-6lTORWjmUPrRGD7k");
                            data.put("template_id", templateId);


                            JSONObject datao = new JSONObject();
                            JSONObject contentData = new JSONObject();
                            contentData.put("value", title);
                            datao.put("title", contentData);

                            contentData = new JSONObject();
                            contentData.put("value", content);
                            datao.put("content", contentData);
                            data.put("data", datao);


                            String url="https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+getResult.result();
                            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create(url))
                                    .timeout(Duration.ofSeconds(10))
                                    .header("Content-Type","application/json")
                                    .POST(HttpRequest.BodyPublishers.ofString( data.toString()))
                                    .build();
                            HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                    .whenComplete((stringHttpResponse, throwable) -> {
                                        if (throwable != null) {
                                            throwable.printStackTrace();
                                        }
                                        if (stringHttpResponse != null) {
                                            String body = stringHttpResponse.body();
                                            LOGGER.info(body);
                                        }
                                    });
                        } else {
                            LOGGER.error("Failed to get accessToken from shared data");
                        }
                    });
                } else {
                    LOGGER.error("Failed to get async map");
                }
            });


            rc.next();
        });

        bookRouter.get("/*").handler(rc -> {
            rc.response().setStatusCode(404).end("Custom 404 message");
        });
        return bookRouter;
    }

}
