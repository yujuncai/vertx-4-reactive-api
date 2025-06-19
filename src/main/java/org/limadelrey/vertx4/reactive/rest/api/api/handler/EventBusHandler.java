package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.model.Book;
import org.limadelrey.vertx4.reactive.rest.api.api.service.BookService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.vos.AnswerParam;

import java.time.Instant;

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
        JsonObject roles = body.getJsonObject("roles");
        String  pingId=  body.getString("pingId");


        JsonObject  dify_json = new JsonObject();
        JsonObject  inputs = new JsonObject();
        inputs.put("roles",roles.getString("role_prompt"));
        dify_json.put("inputs",inputs);
        dify_json.put("query","开始扮演");
        dify_json.put("conversation_id","");
        dify_json.put("user",roles.getString("role_name"));


        Future<AnswerParam> source = chatToDify(sourceJson,dify_json);
        source.onSuccess(answerParam -> {
            LOGGER.info("answerParam----> {}", answerParam);
           /* Book book = new Book();
            book.setAgentId(sourceJson.getString("apikey"));
            book.setAnswer(answerParam.getAnswer());
            book.setQuerys(dify_json.getString("query"));
            book.setType(0);
            book.setCreateTime(Instant.now());
            book.setPingId(pingId);
            bookService.create(book);*/
            //初始化0
            dify_json.put("conversation_id",answerParam.getCoverId());
            body.put("source_dify_json",dify_json);
            //初始化1
            JsonObject  target_dify_json = new JsonObject();
            JsonObject  tinputs = new JsonObject();
            tinputs.put("history","");
            target_dify_json.put("inputs",tinputs);
            target_dify_json.put("conversation_id","");
            target_dify_json.put("user",roles.getString("role_name"));
            target_dify_json.put("query",answerParam.getAnswer());
            body.put("target_dify_json",target_dify_json);
            vertx.eventBus().send("chat_to_1", body);

        }).onFailure(throwable -> {
            LOGGER.info("Error ", throwable);
        });
    }





    public void handler0(Message<Object> message){

        LOGGER.info("Message received: {}", message.body().toString());
        JsonObject body = (JsonObject) message.body();
        JsonObject sourceJson = body.getJsonObject("source");
        JsonObject source_dify_json = body.getJsonObject("source_dify_json");

        Future<AnswerParam> source = chatToDify(sourceJson,source_dify_json);
        source.onSuccess(answerParam -> {
            LOGGER.info("INFO 0 {}", answerParam);
           /* Book book = new Book();
            book.setAgentId(sourceJson.getString("apikey"));
            book.setAnswer(answerParam.getAnswer());
            book.setQuerys(source_dify_json.getString("query"));
            book.setType(0);
            book.setCreateTime(Instant.now());
            book.setPingId(body.getString("pingId"));
            bookService.create(book);*/
            //设置cover
            source_dify_json.put("conversation_id",answerParam.getCoverId());

            JsonObject target_dify_json = body.getJsonObject("target_dify_json");
            target_dify_json.put("query",answerParam.getAnswer());
            vertx.eventBus().send("chat_to_1", body);


        }).onFailure(throwable -> {
            LOGGER.info("Error ", throwable);
        });
    }



    public void handler1(Message<Object> message){

        JsonObject body = (JsonObject) message.body();
        JsonObject targetJson = body.getJsonObject("target");
        JsonObject target_dify_json = body.getJsonObject("target_dify_json");


        Future<AnswerParam> target = chatToDify(targetJson,target_dify_json);
        target.onSuccess(answerParam -> {
            LOGGER.info("INFO 1 {}", answerParam);
            Book book = new Book();
            book.setAgentId(targetJson.getString("apikey"));
            book.setAnswer(answerParam.getAnswer());
            book.setQuerys(target_dify_json.getString("query"));
            book.setType(1);
            book.setCreateTime(Instant.now());
            book.setPingId(body.getString("pingId"));
            bookService.create(book);

            target_dify_json.put("conversation_id",answerParam.getCoverId());



           /* HttpServerResponse sse = SseMap.sseClients.get(targetJson.getString("pingId"));
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
            }*/




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


            JsonObject sourceJson = body.getJsonObject("source_dify_json");
            sourceJson.put("query",answerParam.getAnswer());
            vertx.eventBus().send("chat_to_0", body);





        }).onFailure(throwable -> {
            LOGGER.info("Error ", throwable);
        });
    }





    private Future<AnswerParam> chatToDify(JsonObject messageBody,JsonObject difyJson) {

        Integer port = messageBody.getInteger("port");
        String host = messageBody.getString("hosts");
        String uri = messageBody.getString("uri");
        String apikey = messageBody.getString("apikey");


        final WebClient webClient = WebClient.create(vertx);
        Promise<AnswerParam> promise = Promise.promise();
        webClient.post(port, host, uri)
                .putHeader("Authorization", "Bearer " + apikey)
                .as(BodyCodec.jsonObject())
                .sendJsonObject(difyJson)
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
