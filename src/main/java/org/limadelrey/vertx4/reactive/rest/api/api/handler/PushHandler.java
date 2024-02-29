package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.R.Result;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class PushHandler {


    private static final Logger LOGGER = LogManager.getLogger(PushHandler.class);


    public PushHandler() {

    }


    public Future<Void> pushToWeChat(RoutingContext rc) {
      return   toWeChat(rc)
              .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result().ok(success)))
              .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }

    private Future<Void> toWeChat(RoutingContext rc) {

            String title = rc.queryParams().get("title");
            String content = rc.queryParams().get("content");
            Vertx vertx = Vertx.currentContext().owner();
            final String templateId = "CTbywqtbXji9rVEkJMvSYG7IXUyy1XHtsEzJiB8pfVI";

            AtomicBoolean isok= new AtomicBoolean(true);
           vertx.sharedData().getAsyncMap("preMap", result -> {
                if (result.succeeded()) {
                    AsyncMap<Object, Object> asyncMap = result.result();
                    asyncMap.get("accessToken", getResult -> {
                        if (getResult.succeeded()) {
                            LOGGER.info("succeeded {}",getResult);

                            if(ObjectUtil.isNull(getResult.result()))
                            {
                                isok.set(false);
                                LOGGER.error("获取accessToken 失败");
                              return;
                            }
                            JSONObject data = getEntries(templateId, title, content);
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


           if(isok.get()){
               return Future.succeededFuture();
           }else {
               return Future.failedFuture("获取Token失败!");
           }



    }



    private  JSONObject getEntries(String templateId, String title, String content) {
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
        return data;
    }


}
