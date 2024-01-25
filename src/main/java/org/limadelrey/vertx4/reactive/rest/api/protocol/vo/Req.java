package org.limadelrey.vertx4.reactive.rest.api.protocol.vo;

import java.io.Serializable;

public class Req  implements Serializable {

    private  String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
