package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
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
import org.limadelrey.vertx4.reactive.rest.api.api.model.BookGetAllResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.model.BookGetByIdResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.model.PingList;
import org.limadelrey.vertx4.reactive.rest.api.api.service.BookService;
import org.limadelrey.vertx4.reactive.rest.api.api.service.PingListService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.vos.AnswerParam;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;


public class EventBusHandler {


    public EventBusHandler() {

    }
    private static final Logger LOGGER = LogManager.getLogger(EventBusHandler.class);
    private final BookService bookService= GuiceUtil.getGuice().getInstance(BookService.class);
    private final Vertx vertx=  Vertx.currentContext().owner();
    private final PingListService pingListService= GuiceUtil.getGuice().getInstance(PingListService.class);




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




        Future<String> future = xBookList(body.getString("pingId"), target_dify_json);
        future.onSuccess(result -> {
            //灌history
            LOGGER.info("result---------------->  {}", result);
            target_dify_json.put("history", result);
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
                    LOGGER.info("response=========={}", response.body());
                    String an= response.body().getString("answer");
                    String cover= response.body().getString("conversation_id");
                    promise.complete( AnswerParam.builder().answer(an).coverId(cover).build());
                }).onFailure(throwable -> {
                    LOGGER.info("Error", throwable);
                });
        return  promise.future();
    }




    public void analysis(Message<Object> message){
        JsonObject body = (JsonObject) message.body();
        String history = body.getString("history");
        String role = body.getString("role");
        JsonObject pingList = body.getJsonObject("pingList");


        JsonObject  dify_json = new JsonObject();
        JsonObject  inputs = new JsonObject();
        inputs.put("chatlist",history);
        inputs.put("role",role);
        dify_json.put("inputs",inputs);
        dify_json.put("query","开始分析");
        dify_json.put("conversation_id","");
        dify_json.put("user","user");
        Future<AnswerParam> source = chatToDify(body,dify_json);
        source.onSuccess(answerParam -> {
            LOGGER.info("body-------{}",body);
            LOGGER.info("对话历史-------{}",history);
            LOGGER.info("角色信息-------{}",role);
            LOGGER.info("分析报告-------{}",answerParam.getAnswer());
            LOGGER.info("pingList-------{}",pingList);

            PingList p=new PingList();
            p.setReports(answerParam.getAnswer());
            p.setStatus("1");
            p.setRoleId(pingList.getString("role_id"));
           p.setTargetId(pingList.getString("target_id"));
           p.setSourceId(pingList.getString("source_id"));
           p.setDescInfo(pingList.getString("desc_info"));
            pingListService.update(pingList.getString("ping_id"), p);
        });




    }













    private Future<String>   xBookList(String pingId,JsonObject target_dify_json){
        Promise promise = Promise.promise();
        Future<BookGetAllResponse> bookGetAllResponseFuture = bookService.readAll("1", "10", pingId).onSuccess(s -> {
            String history = "";

            if(CollectionUtil.isEmpty(s.getBooks())){
                promise.future();
            }

            List<BookGetByIdResponse> list = s.getBooks().stream()
                    .sorted(Comparator.comparing(BookGetByIdResponse::getId))
                    .toList();
            for (BookGetByIdResponse book : list) {
              /*  "
                'time: 2025-06-19 20:21:06.000',
                 'user: 我要给张三转账',
                 'assistant: 您要转账的张三，我这边没有找到匹配的转账好友，请提供收款人账号或确认是否为陌生人转账。',
                "*/

                String answer = book.getAnswer();
                if(StrUtil.isNotBlank(answer)&&answer.contains("@!@")){
                    String[] split = answer.split("@!@");
                        if(split.length>=1){
                            answer=split[0];
                        }
                }

                String x = String.format("'time: %s'\n'user: %s'\n'assistant: %s'\n",
                        book.getCreateTime().toString(),
                        book.getQuerys(),
                        answer);
                history += x;
            }
            promise.complete(history);
        });

        return promise.future();
    }
}
