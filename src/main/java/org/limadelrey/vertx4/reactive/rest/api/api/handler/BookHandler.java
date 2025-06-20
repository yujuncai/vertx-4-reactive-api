package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.google.inject.Singleton;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.limadelrey.vertx4.reactive.rest.api.R.Result;
import org.limadelrey.vertx4.reactive.rest.api.api.model.*;
import org.limadelrey.vertx4.reactive.rest.api.api.service.AgentInfosService;
import org.limadelrey.vertx4.reactive.rest.api.api.service.BookService;
import org.limadelrey.vertx4.reactive.rest.api.api.service.PingListService;
import org.limadelrey.vertx4.reactive.rest.api.api.service.RolesService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;

import java.util.Comparator;
import java.util.List;

@Singleton
public class BookHandler {

    private static final String ID_PARAMETER = "id";
    private static final String PAGE_PARAMETER = "page";
    private static final String LIMIT_PARAMETER = "limit";
    private final Vertx vertx=  Vertx.currentContext().owner();
    private final BookService bookService= GuiceUtil.getGuice().getInstance(BookService.class);
    private final AgentInfosService agentInfosService= GuiceUtil.getGuice().getInstance(AgentInfosService.class);
    private final PingListService pingListService= GuiceUtil.getGuice().getInstance(PingListService.class);
    private final RolesService rolesService= GuiceUtil.getGuice().getInstance(RolesService.class);


    public BookHandler() {

    }

    /**
     * Read all books
     * It should return 200 OK in case of success
     * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
     *
     * @param rc Routing context
     * @return BookGetAllResponse
     */
    public Future<BookGetAllResponse> readAll(RoutingContext rc) {
        final String page = StrUtil.isEmpty(rc.pathParam(PAGE_PARAMETER))?rc.queryParams().get(PAGE_PARAMETER):rc.pathParam(PAGE_PARAMETER);
        final String limit = StrUtil.isEmpty(rc.pathParam(LIMIT_PARAMETER))?rc.queryParams().get(LIMIT_PARAMETER):rc.pathParam(LIMIT_PARAMETER);
        final String pingId = StrUtil.isEmpty(rc.pathParam("pingId"))?rc.queryParams().get("pingId"):rc.pathParam("pingId");
        return bookService.readAll(page, limit,pingId)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<BookGetAllResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }

    /**
     * Read one book
     * It should return 200 OK in case of success
     * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
     *
     * @param rc Routing context
     * @return BookGetByIdResponse
     */
    public Future<BookGetByIdResponse> readOne(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);

        return bookService.readOne(Integer.parseInt(id))
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc,  new Result<BookGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }



    public Future<Void> analysis(RoutingContext rc) {
        final String pingId = rc.pathParam("pingId");

        Future<List<BookGetByIdResponse>> all = bookService.getAll(pingId);
        Future<AgentInfosGetAllResponse> agentInfos = agentInfosService.readAll("1", "1", "2");
        Future<PingListGetByIdResponse> pingFuture = pingListService.readOne(pingId).onSuccess(ping -> {

            System.out.println("########################################"+ping.getRoleId());
            Future<RolesGetByIdResponse> roles = rolesService.readOne(ping.getRoleId());
            CompositeFuture.all(all,agentInfos,roles).onComplete(ar -> {
                if (ar.succeeded()) {
                    List<BookGetByIdResponse> result1 = ar.result().resultAt(0);
                    AgentInfosGetAllResponse result2 = ar.result().resultAt(1);
                    RolesGetByIdResponse result3 = ar.result().resultAt(2);
                    if(result3==null){
                        ResponseUtils.buildErrResponse(rc,"角色不存在");
                    }

                    if(result2==null){
                        ResponseUtils.buildErrResponse(rc,"agentInfos不存在");
                    }
                    if(CollectionUtil.isEmpty(result1)){
                        ResponseUtils.buildErrResponse(rc,"没有历史记录可以分析！");
                    }

                    AgentInosGetByIdResponse first = result2.getAgents().getFirst();
                    JsonObject entries = JsonObject.mapFrom(first);

                    List<BookGetByIdResponse> list = result1.stream()
                            .sorted(Comparator.comparing(BookGetByIdResponse::getId))
                            .toList();
                    String history = "";
                    for (BookGetByIdResponse book : list) {
                        String x = String.format("'time: %s'\n'user: %s'\n'assistant: %s'\n",
                                book.getCreateTime().toString(),
                                book.getQuerys(),
                                book.getAnswer());
                        history += x;
                    }
                    entries.put("pingList",JsonObject.mapFrom(ping));
                    entries.put("history",history);
                    entries.put("role",result3.getRolePrompt());
                    vertx.eventBus().send("analysis", entries);
                    ResponseUtils.buildOkResponse(rc, new Result<Void>().ok(null));
                }
            }).onFailure(cause -> {
                // 错误统一处理
                ResponseUtils.buildErrorResponse(rc, cause);
            });

        });

        return Future.succeededFuture();
    }






    public Future<BookGetByIdResponse> create(RoutingContext rc) {
        final Book book = rc.getBodyAsJson().mapTo(Book.class);

        return bookService.create(book)
                .onSuccess(success -> ResponseUtils.buildCreatedResponse(rc, new Result<BookGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable))
        ;
    }

    /**R
     * Update one book
     * It should return 200 OK in case of success
     * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
     *
     * @param rc Routing context
     * @return BookGetByIdResponse
     */
    public Future<BookGetByIdResponse> update(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);
        final Book book = rc.getBodyAsJson().mapTo(Book.class);

        return bookService.update(Integer.parseInt(id), book)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<BookGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }

    /**
     * Delete one book
     * It should return 204 No Content in case of success
     * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
     *
     * @param rc Routing context
     * @return BookGetByIdResponse
     */
    public Future<Void> delete(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);


        return bookService.delete(Integer.parseInt(id))
                .onSuccess(success -> ResponseUtils.buildNoContentResponse(rc))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }

}
