package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.google.inject.Singleton;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.R.Result;
import org.limadelrey.vertx4.reactive.rest.api.api.model.AgentInfosGetAllResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.model.AgentInosGetByIdResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.service.AgentInfosService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;
import org.limadelrey.vertx4.reactive.rest.api.vos.EvevtParam;
import org.limadelrey.vertx4.reactive.rest.api.vos.QueryParam;

import java.util.List;

@Singleton
public class AagentInfosHandler {

    private static final String ID_PARAMETER = "id";
    private static final String PAGE_PARAMETER = "page";
    private static final String LIMIT_PARAMETER = "limit";

    private final AgentInfosService service= GuiceUtil.getGuice().getInstance(AgentInfosService.class);
    private static final Logger LOGGER = LogManager.getLogger(AagentInfosHandler.class);
    private final Vertx vertx=  Vertx.currentContext().owner();
    public AagentInfosHandler() {

    }


    public Future<AgentInfosGetAllResponse> readAll(RoutingContext rc) {
        final String page = rc.queryParams().get(PAGE_PARAMETER);
        final String limit = rc.queryParams().get(LIMIT_PARAMETER);

        return service.readAll(page, limit)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<AgentInfosGetAllResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }


    public Future<AgentInosGetByIdResponse> readOne(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);

        return service.readOne(Long.parseLong(id))
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc,  new Result<AgentInosGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }




    public Future<Void> delete(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);


        return service.delete(Long.parseLong(id))
                .onSuccess(success -> ResponseUtils.buildNoContentResponse(rc))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }


    public CompositeFuture chatToAgent(RoutingContext rc) {
        final QueryParam param  = rc.body().asJsonObject().mapTo(QueryParam.class);
        Future<JsonObject> sourceFuture = service.selectByType("0",param.getAction()).map(m -> buildJson(m,param));
        Future<JsonObject> targetFuture = service.selectByType("1",param.getAction()).map(m -> buildJson(m,param));





        return     CompositeFuture.all(sourceFuture, targetFuture).onComplete(ar -> {
            if (ar.succeeded()) {
                // 所有的Future都成功完成
                JsonObject result1 = ar.result().resultAt(0);
                JsonObject result2 = ar.result().resultAt(1);
                 EvevtParam build =  EvevtParam.builder().source(result1).target(result2).build();


                sendEventBusMessage( JsonObject.mapFrom(build));
                ResponseUtils.buildOkResponse(rc,new Result<EvevtParam>().ok(build));

            } else {
                // 至少有一个Future失败了
                Throwable cause = ar.cause();
                ResponseUtils.buildErrorResponse(rc, cause);
            }
        }).onFailure(cause -> {
            // 错误统一处理
            ResponseUtils.buildErrorResponse(rc, cause);
        });






    }


    
    private Future<Void> sendEventBusMessage(JsonObject build) {
         vertx.eventBus().send("chat_to_dify", build);
        return Future.succeededFuture();
    }


    private JsonObject buildJson(List<AgentInosGetByIdResponse> m,QueryParam param){
        if (CollectionUtil.isEmpty(m)) {
            throw new RuntimeException("No agent available");
        }
        AgentInosGetByIdResponse first = m.getFirst();
        JsonObject json = new JsonObject();
        json.put("apikey", first.getApikey());
        json.put("uri", first.getUri());
        json.put("hosts", first.getHosts());
        json.put("port", first.getPort());
        JsonObject  body = new JsonObject();
        body.put("inputs","");
        body.put("query",param.getQuerys());
        body.put("conversation_id","");
        body.put("user","1001827281");
        json.put("body",body);
        return json;
    }
    
}
