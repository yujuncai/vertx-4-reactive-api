package org.limadelrey.vertx4.reactive.rest.api.verticle;

import cn.hutool.json.JSONUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.shareddata.SharedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.CronExpression;
import org.limadelrey.vertx4.reactive.rest.api.utils.ConfigUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

public class PreVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LogManager.getLogger(PreVerticle.class);

    private static final String APPID = "wx.appID";
    private static final String APPSECRET = "wx.appsecret";
    @Override
    public void start(Promise<Void> promise) {

        vertx.setPeriodic(1000*60*60, id -> {

            extracted(promise);
        });


        extracted(promise);

    }

    private void extracted(Promise<Void> promise) {
        final Properties properties = ConfigUtils.getInstance().getProperties();

        final String appID =  properties.getProperty(APPID);
        ;
        final String appSecret =  properties.getProperty(APPSECRET);

        String wechat_token_url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appID+"&secret="+appSecret;

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

                       if( stringHttpResponse.statusCode()==200){

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
                            promise.complete();
                       }

                    }
                });
    }


}






