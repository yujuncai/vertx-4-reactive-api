package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.google.inject.Singleton;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.R.Result;
import org.limadelrey.vertx4.reactive.rest.api.api.model.*;
import org.limadelrey.vertx4.reactive.rest.api.api.service.AgentInfosService;
import org.limadelrey.vertx4.reactive.rest.api.api.service.PingListService;
import org.limadelrey.vertx4.reactive.rest.api.api.service.RolesService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;
import org.limadelrey.vertx4.reactive.rest.api.vos.EvevtParam;
import org.limadelrey.vertx4.reactive.rest.api.vos.QueryParam;

@Singleton
public class AagentInfosHandler {

    private static final String ID_PARAMETER = "id";
    private static final String PAGE_PARAMETER = "page";
    private static final String LIMIT_PARAMETER = "limit";

    private final AgentInfosService service= GuiceUtil.getGuice().getInstance(AgentInfosService.class);

    private final RolesService rolesService= GuiceUtil.getGuice().getInstance(RolesService.class);

    private final PingListService pingListService= GuiceUtil.getGuice().getInstance(PingListService.class);

    private static final Logger LOGGER = LogManager.getLogger(AagentInfosHandler.class);
    private final Vertx vertx=  Vertx.currentContext().owner();
    public AagentInfosHandler() {

    }


    public Future<AgentInfosGetAllResponse> readAll(RoutingContext rc) {
        final String page = StrUtil.isEmpty(rc.pathParam(PAGE_PARAMETER))?rc.queryParams().get(PAGE_PARAMETER):rc.pathParam(PAGE_PARAMETER);
        final String limit = StrUtil.isEmpty(rc.pathParam(LIMIT_PARAMETER))?rc.queryParams().get(LIMIT_PARAMETER):rc.pathParam(LIMIT_PARAMETER);

        return service.readAll(page, limit)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<AgentInfosGetAllResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }


    public Future<AgentInosGetByIdResponse> readOne(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);

        return service.readOne(id)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc,  new Result<AgentInosGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }

    public Future<AgentInosGetByIdResponse> create(RoutingContext rc) {

        final AgentInfos book = rc.getBodyAsJson().mapTo(AgentInfos.class);

        return service.create(book)
                .onSuccess(success -> ResponseUtils.buildCreatedResponse(rc, new Result<AgentInosGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable))
                ;
    }
    public Future<AgentInosGetByIdResponse> update(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);
        final AgentInfos book = rc.getBodyAsJson().mapTo(AgentInfos.class);

        return service.update(id, book)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<AgentInosGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }

    public Future<Void> delete(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);


        return service.delete(id)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc,new Result<String>().ok(id)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }


    public CompositeFuture chatToAgent(RoutingContext rc) {
        final QueryParam param  = rc.body().asJsonObject().mapTo(QueryParam.class);

        Future<RolesGetByIdResponse> rolesFuture = rolesService.readOne(param.getRolesId());
        Future<AgentInosGetByIdResponse> sourceFuture = service.readOne(param.getSid());
        Future<AgentInosGetByIdResponse> targetFuture = service.readOne(param.getTid());

        return     CompositeFuture.all(rolesFuture,sourceFuture, targetFuture).onComplete(ar -> {
            if (ar.succeeded()) {
                // 所有的Future都成功完成
                RolesGetByIdResponse result1 = ar.result().resultAt(0);
                AgentInosGetByIdResponse result2 = ar.result().resultAt(1);
                AgentInosGetByIdResponse result3 = ar.result().resultAt(2);
              String pingId=  UUID.fastUUID().toString();
                //插入流水
                PingList p=new PingList();
                p.setPingId(pingId);
                p.setRoleId(param.getRolesId());
                p.setSourceId(param.getSid());
                p.setTargetId(param.getTid());
                p.setDescInfo(param.getDesc());
                PingListGetByIdResponse result = pingListService.create(p).result();

                JsonObject roleInfo = JsonObject.mapFrom(result1);
                JsonObject sourceJson = JsonObject.mapFrom(result2);
                JsonObject targetJson = JsonObject.mapFrom(result3);


                EvevtParam  build=  EvevtParam.builder()
                        .roles(roleInfo)
                        .source(sourceJson).
                        target(targetJson)
                        .loop(param.getLoop())
                        .pingId(pingId)
                        .build();
                //异步发给seventBus
                LOGGER.info("INFO Start {}", JsonObject.mapFrom(build));
                sendEventBusMessage( JsonObject.mapFrom(build));
                ResponseUtils.buildOkResponse(rc,new Result<String>().ok(pingId));
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



    
}
