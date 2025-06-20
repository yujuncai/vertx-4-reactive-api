package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import cn.hutool.core.util.StrUtil;
import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import org.limadelrey.vertx4.reactive.rest.api.R.Result;
import org.limadelrey.vertx4.reactive.rest.api.api.model.Roles;
import org.limadelrey.vertx4.reactive.rest.api.api.model.RolesGetAllResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.model.RolesGetByIdResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.service.RolesService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;

@Singleton
public class RolesHandler {

    private static final String ID_PARAMETER = "id";
    private static final String PAGE_PARAMETER = "page";
    private static final String LIMIT_PARAMETER = "limit";

    private final RolesService service= GuiceUtil.getGuice().getInstance(RolesService.class);


    public RolesHandler() {

    }

    /**
     * Read all books
     * It should return 200 OK in case of success
     * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
     *
     * @param rc Routing context
     * @return BookGetAllResponse
     */
    public Future<RolesGetAllResponse> readAll(RoutingContext rc) {
        final String page = StrUtil.isEmpty(rc.pathParam(PAGE_PARAMETER))?rc.queryParams().get(PAGE_PARAMETER):rc.pathParam(PAGE_PARAMETER);
        final String limit = StrUtil.isEmpty(rc.pathParam(LIMIT_PARAMETER))?rc.queryParams().get(LIMIT_PARAMETER):rc.pathParam(LIMIT_PARAMETER);
        return service.readAll(page, limit)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<RolesGetAllResponse>().ok(success)))
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
    public Future<RolesGetByIdResponse> readOne(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);

        return service.readOne(id)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc,  new Result<RolesGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }

    /**
     * Create one book
     * It should return 201 Created in case of success
     * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
     *
     * @param rc Routing context
     * @return BookGetByIdResponse
     */
    public Future<RolesGetByIdResponse> create(RoutingContext rc) {
        final Roles book = rc.getBodyAsJson().mapTo(Roles.class);

        return service.create(book)
                .onSuccess(success -> ResponseUtils.buildCreatedResponse(rc, new Result<RolesGetByIdResponse>().ok(success)))
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
    public Future<RolesGetByIdResponse> update(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);
        final Roles book = rc.getBodyAsJson().mapTo(Roles.class);

        return service.update(id, book)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<RolesGetByIdResponse>().ok(success)))
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


        return service.delete(id)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<String>().ok(id)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }

}
