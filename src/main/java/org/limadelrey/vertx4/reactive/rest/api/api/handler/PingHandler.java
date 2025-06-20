package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import cn.hutool.core.util.StrUtil;
import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import org.limadelrey.vertx4.reactive.rest.api.R.Result;
import org.limadelrey.vertx4.reactive.rest.api.api.model.PingListGetAllResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.service.PingListService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;

@Singleton
public class PingHandler {

    private static final String ID_PARAMETER = "id";
    private static final String PAGE_PARAMETER = "page";
    private static final String LIMIT_PARAMETER = "limit";

    private final PingListService bookService= GuiceUtil.getGuice().getInstance(PingListService.class);


    public PingHandler() {

    }

    /**
     * Read all books
     * It should return 200 OK in case of success
     * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
     *
     * @param rc Routing context
     * @return BookGetAllResponse
     */
    public Future<PingListGetAllResponse> readAll(RoutingContext rc) {
        final String page = StrUtil.isEmpty(rc.pathParam(PAGE_PARAMETER))?rc.queryParams().get(PAGE_PARAMETER):rc.pathParam(PAGE_PARAMETER);
        final String limit = StrUtil.isEmpty(rc.pathParam(LIMIT_PARAMETER))?rc.queryParams().get(LIMIT_PARAMETER):rc.pathParam(LIMIT_PARAMETER);

        return bookService.readAll(page, limit)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<PingListGetAllResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }



}
