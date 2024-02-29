package org.limadelrey.vertx4.reactive.rest.api.verticle;

import cn.hutool.json.JSONUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PreVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LogManager.getLogger(PreVerticle.class);

    @Override
    public void start(Promise<Void> promise) {

        final String appID = "wx441b43b87703bafb";
        final String appsecret = "670229025cfa1aed29d89be6189f5b3b";
        String wechat_token_url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appID+"&secret="+appsecret;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(wechat_token_url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .whenComplete((stringHttpResponse, throwable) -> {
                        if (throwable != null) {
                            throwable.printStackTrace();
                        }
                        if (stringHttpResponse != null) {
                            String body = stringHttpResponse.body();
                            String    accessToken = (String) JSONUtil.parseObj(body).get("access_token");
                            SharedData sharedData = vertx.sharedData();
                            sharedData.getAsyncMap("preMap", result -> {
                                if (result.succeeded()) {
                                    result.result().put("accessToken", accessToken, putResult -> {
                                        if (putResult.succeeded()) {
                                            LOGGER.info("accessToken  shared successfully");
                                        } else {
                                            LOGGER.info("Failed to share accessToken");
                                        }
                                    });
                                } else {
                                    LOGGER.error("Failed to get async map");
                                }

                            });
                        }
                    });

    }


}






