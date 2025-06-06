package org.limadelrey.vertx4.reactive.rest.api.verticle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.vos.AnswerParam;

public class DifyVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LogManager.getLogger(DifyVerticle.class);


    @Override
    public void start(Promise<Void> promise) {

        ObjectMapper mapper = DatabindCodec.mapper();
        mapper.registerModule(new JavaTimeModule());
        vertx.eventBus().consumer("chat_to_dify").handler(message -> {
            LOGGER.info("Message received: {}", message.body().toString());
            JsonObject body = (JsonObject) message.body();

            Future<AnswerParam> source = chatToDify(body.getJsonObject("source"));



            source.onSuccess(answerParam -> {
                LOGGER.info("INFO {}", answerParam);
            }).onFailure(throwable -> {
                LOGGER.info("Error ", throwable);
            });


            body.getJsonObject("target");

        });
        promise.complete();
        LOGGER.info("Verticle started!");
    }

    private Future<AnswerParam> chatToDify(JsonObject messageBody) {

        Integer port = messageBody.getInteger("port");
        String host = messageBody.getString("hosts");
        String uri = messageBody.getString("uri");
        String apikey = messageBody.getString("apikey");
        JsonObject body=  messageBody.getJsonObject("body");

        final WebClient webClient = WebClient.create(vertx);
        Promise<AnswerParam> promise = Promise.promise();
        webClient.post(port, host, uri)
                .putHeader("Authorization", "Bearer " + apikey)
                .as(BodyCodec.jsonObject())
                .sendJsonObject(body)
                 .onSuccess(response -> {
                    String an= response.body().getString("answer");
                     promise.complete( AnswerParam.builder().answer(an).build());
                 }).onFailure(throwable -> {
                     LOGGER.info("Error", throwable);
                 });
        return  promise.future();
    }




}
