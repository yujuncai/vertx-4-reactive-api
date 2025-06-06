package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.utils.JwtUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;

@Singleton
public class JwtAuthHandler {


    private static final Logger LOGGER = LogManager.getLogger(JwtAuthHandler.class);


    public JwtAuthHandler() {

    }


    public Future<Void> TokenAuth(RoutingContext rc) {
        JWTAuth instance = JwtUtils.getInstance();
        String token = rc.request().getHeader("token");
        Credentials credentials =new TokenCredentials( token);
        Future<User> userFuture = instance.authenticate(credentials)
                .onSuccess(user -> {
                    System.out.println("user:" + user.principal());
                    rc.put("user", user.principal());
                    rc.next();
                }).onFailure(err -> {
                    ResponseUtils.buildErrorResponse(rc,err);
                });
      return  Future.succeededFuture();
    }


    public Future<Void> PageTokenAuth(RoutingContext rc) {
        JWTAuth instance = JwtUtils.getInstance();
        String token = rc.request().getHeader("token");
        Credentials credentials =new TokenCredentials( token);
        Future<User> userFuture = instance.authenticate(credentials)
                .onSuccess(user -> {
                    System.out.println("user:" + user.principal());
                    rc.put("user", user.principal());
                    rc.next();
                }).onFailure(err -> {
                    ResponseUtils.buildRedirectResponse(rc);
                });
        return  Future.succeededFuture();
    }

}
