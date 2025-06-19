package org.limadelrey.vertx4.reactive.rest.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class RolesGetAllResponse implements Serializable {

    private static final long serialVersionUID = -8964658883487451260L;

    @JsonProperty(value = "total")
    private final int total;

    @JsonProperty(value = "limit")
    private final int limit;

    @JsonProperty(value = "page")
    private final int page;

    @JsonProperty(value = "roles")
    private final List<RolesGetByIdResponse> roles;

    public RolesGetAllResponse(int total,
                               int limit,
                               int page,
                               List<RolesGetByIdResponse> books) {
        this.total = total;
        this.limit = limit;
        this.page = page;
        this.roles = books;
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

    public List<RolesGetByIdResponse> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolesGetAllResponse that = (RolesGetAllResponse) o;
        return total == that.total &&
                limit == that.limit &&
                page == that.page &&
                roles.equals(that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, limit, page, roles);
    }

    @Override
    public String toString() {
        return "BookGetAllResponse{" +
                "total=" + total +
                ", limit=" + limit +
                ", page=" + page +
                ", roles=" + roles +
                '}';
    }

}
