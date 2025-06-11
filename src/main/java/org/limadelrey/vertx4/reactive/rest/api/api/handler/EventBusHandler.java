package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.model.Book;
import org.limadelrey.vertx4.reactive.rest.api.api.service.BookService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.SseMap;
import org.limadelrey.vertx4.reactive.rest.api.vos.AnswerParam;

public class EventBusHandler {
    public EventBusHandler() {

    }
    private static final Logger LOGGER = LogManager.getLogger(EventBusHandler.class);
    private final BookService bookService= GuiceUtil.getGuice().getInstance(BookService.class);
    private final Vertx vertx=  Vertx.currentContext().owner();





    public void handlerStart(Message<Object> message){
        LOGGER.info("Message received: {}", message.body().toString());
        JsonObject body = (JsonObject) message.body();
        JsonObject sourceJson = body.getJsonObject("source");
        JsonObject targetJson = body.getJsonObject("target");

        String  pingId=  body.getString("pingId");

        sourceJson.put("pingId",pingId);
        targetJson.put("pingId",pingId);

        Future<AnswerParam> source = chatToDify(sourceJson);
        source.onSuccess(answerParam -> {
            LOGGER.info("INFO Start {}", answerParam);
            Book book = new Book();
            book.setAgentId(sourceJson.getString("apikey"));
            book.setAnswer(answerParam.getAnswer());
            book.setQuerys(sourceJson.getJsonObject("body").getString("query"));
            book.setType(0);
            book.setCreateTime(DateUtil.date());
            book.setPingId(pingId);
            bookService.create(book);

            sourceJson.getJsonObject("body").put("conversation_id",answerParam.getCoverId());
            //回答替换为输入
            targetJson.getJsonObject("body").put("query",answerParam.getAnswer());

            vertx.eventBus().send("chat_to_1", body);

        }).onFailure(throwable -> {
            LOGGER.info("Error ", throwable);
        });
    }





    public void handler0(Message<Object> message){

        LOGGER.info("Message received: {}", message.body().toString());
        JsonObject body = (JsonObject) message.body();
        JsonObject sourceJson = body.getJsonObject("source");

        Future<AnswerParam> source = chatToDify(sourceJson);
        source.onSuccess(answerParam -> {
            LOGGER.info("INFO 0 {}", answerParam);
            Book book = new Book();
            book.setAgentId(sourceJson.getString("apikey"));
            book.setAnswer(answerParam.getAnswer());
            book.setQuerys(sourceJson.getJsonObject("body").getString("query"));
            book.setType(0);
            book.setCreateTime(DateUtil.date());
            book.setPingId(sourceJson.getString("pingId"));
            bookService.create(book);
            //设置cover
            sourceJson.getJsonObject("body").put("conversation_id",answerParam.getCoverId());

            JsonObject targetJson = body.getJsonObject("target");
            targetJson.getJsonObject("body").put("query",answerParam.getAnswer());
            vertx.eventBus().send("chat_to_1", body);


        }).onFailure(throwable -> {
            LOGGER.info("Error ", throwable);
        });
    }



    public void handler1(Message<Object> message){

        JsonObject body = (JsonObject) message.body();
        JsonObject targetJson = body.getJsonObject("target");

        Future<AnswerParam> target = chatToDify(targetJson);
        target.onSuccess(answerParam -> {
            LOGGER.info("INFO 1 {}", answerParam);
            Book book = new Book();
            book.setAgentId(targetJson.getString("apikey"));
            book.setAnswer(answerParam.getAnswer());
            book.setQuerys(targetJson.getJsonObject("body").getString("query"));
            book.setType(1);
            book.setCreateTime(DateUtil.date());
            book.setPingId(targetJson.getString("pingId"));
            bookService.create(book);

            targetJson.getJsonObject("body").put("conversation_id",answerParam.getCoverId());



            HttpServerResponse sse = SseMap.sseClients.get(targetJson.getString("pingId"));
            if(!sse.closed()) {
                JSONObject entries = JSONUtil.parseObj(book);
                entries.put("type", "history-item");
                String data = JSONUtil.toJsonStr(entries);
                String event = """
                        data: %s
                        event: history-data
                        \n\n
                        """.formatted(data);
                LOGGER.info("---------发送数据-------------- {}", event);
                sse.write(event);
            }




            if(answerParam.getAnswer().contains("请点击立即转账")){
                LOGGER.info("INFO 1 {}", "转账流程以是最后一步，结束测试！");
                return;
            }



            body.put("loop",body.getInteger("loop")-1);
            Integer loop = body.getInteger("loop");
            if(loop<0){
                LOGGER.info("INFO 1 {}", "looped!!!!!!!!!!!!!!!!!!!！");
                return;
            }


            JsonObject sourceJson = body.getJsonObject("source");
            sourceJson.getJsonObject("body").put("query",answerParam.getAnswer());
            vertx.eventBus().send("chat_to_0", body);





        }).onFailure(throwable -> {
            LOGGER.info("Error ", throwable);
        });
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

                    String cover= response.body().getString("conversation_id");

                    promise.complete( AnswerParam.builder().answer(an).coverId(cover).build());
                }).onFailure(throwable -> {
                    LOGGER.info("Error", throwable);
                });
        return  promise.future();
    }


}
