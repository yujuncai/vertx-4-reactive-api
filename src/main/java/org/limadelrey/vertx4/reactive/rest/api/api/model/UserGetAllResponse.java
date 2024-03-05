package org.limadelrey.vertx4.reactive.rest.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class UserGetAllResponse implements Serializable {

    private static final long serialVersionUID = -8964658883487451260L;

    @JsonProperty(value = "total")
    private final int total;

    @JsonProperty(value = "limit")
    private final int limit;

    @JsonProperty(value = "page")
    private final int page;

    @JsonProperty(value = "users")
    private final List<UserGetByIdResponse> users;

    public UserGetAllResponse(int total,
                              int limit,
                              int page,
                              List<UserGetByIdResponse> users) {
        this.total = total;
        this.limit = limit;
        this.page = page;
        this.users = users;
    }

    public int getTotal() {
        return total;
    }

    public int getLimit() {
        return limit;
    }

    public int getPage() {
        return page;
    }

    public List<UserGetByIdResponse> getUsers() {
        return users;
    }



}
