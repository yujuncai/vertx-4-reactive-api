package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import cn.hutool.crypto.SecureUtil;
import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.web.RoutingContext;
import org.limadelrey.vertx4.reactive.rest.api.R.Result;
import org.limadelrey.vertx4.reactive.rest.api.api.model.*;
import org.limadelrey.vertx4.reactive.rest.api.api.service.BookService;
import org.limadelrey.vertx4.reactive.rest.api.api.service.UserService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.JwtUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;

@Singleton
public class UserHandler {

    private static final String ID_PARAMETER = "id";
    private static final String PAGE_PARAMETER = "page";
    private static final String LIMIT_PARAMETER = "limit";

    private final UserService userService= GuiceUtil.getGuice().getInstance(UserService.class);


    public UserHandler() {

    }


    public Future<UserGetAllResponse> readAll(RoutingContext rc) {
        final String page = rc.queryParams().get(PAGE_PARAMETER);
        final String limit = rc.queryParams().get(LIMIT_PARAMETER);

        return userService.readAll(page, limit)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<UserGetAllResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }


    public Future<UserGetByIdResponse> readOne(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);

        return userService.readOne(Integer.parseInt(id))
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc,  new Result<UserGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }


    public Future<UserGetByIdResponse> create(RoutingContext rc) {
        final User u = rc.getBodyAsJson().mapTo(User.class);

        return userService.create(u)
                .onSuccess(success -> ResponseUtils.buildCreatedResponse(rc, new Result<UserGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }


    public Future<UserGetByIdResponse> update(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);
        final User u = rc.getBodyAsJson().mapTo(User.class);

        return userService.update(Integer.parseInt(id), u)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<UserGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }


    public Future<Void> delete(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);

        return userService.delete(Long.parseLong(id))
                .onSuccess(success -> ResponseUtils.buildNoContentResponse(rc))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }



    public Future<UserGetByIdResponse> login(RoutingContext rc) {

        final User u = rc.getBodyAsJson().mapTo(User.class);

        return    userService.login(u.getUserName()).onSuccess(success -> {
                     String sha1Hex = SecureUtil.sha1(u.getPassword());


                     if(success.getId()==null){
                         ResponseUtils.buildErrResponse(rc, "无此用户!");
                     }else {

                         if (success.getPassword().equals(sha1Hex)) {

                             JsonObject json = new JsonObject().put("userName", u.getUserName());
                             Credentials credentials = new UsernamePasswordCredentials(json);
                             String token = JwtUtils.getInstance().generateToken(credentials.toJson());
                             ResponseUtils.buildCreatedResponse(rc, new Result<String>().ok(token));
                         } else {
                             ResponseUtils.buildErrResponse(rc, "密码错误!");
                         }
                     }
         })
         .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));





    }


}
