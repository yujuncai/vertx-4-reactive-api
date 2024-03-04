package org.limadelrey.vertx4.reactive.rest.api.utils;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.pgclient.PgPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JwtUtils {

    private static final Logger LOGGER = LogManager.getLogger(JwtUtils.class);

    private static final JwtUtils instance = new JwtUtils();
    private volatile  static JWTAuth auth ;
    public static JWTAuth getInstance() {

        if (auth == null) {
            synchronized (JwtUtils.class) {
                if (auth == null) {

                    auth = instance.buildAuth();
                }
            }
        }
        return auth;
    }

    private JWTAuth buildAuth() {
        LOGGER.info("Building JWT Auth");
        JWTAuth provider = JWTAuth.create(Vertx.currentContext().owner(), new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions()
                        .setAlgorithm("HS256")
                        .setBuffer("vertx")));
        return  provider;
    }

}
