package org.limadelrey.vertx4.reactive.rest.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class PingListGetAllResponse implements Serializable {

    private static final long serialVersionUID = -8964658883487451260L;

    @JsonProperty(value = "total")
    private final int total;

    @JsonProperty(value = "limit")
    private final int limit;

    @JsonProperty(value = "page")
    private final int page;

    @JsonProperty(value = "lists")
    private final List<PingListGetByIdResponse> lists;

    public PingListGetAllResponse(int total,
                                  int limit,
                                  int page,
                                  List<PingListGetByIdResponse> lists) {
        this.total = total;
        this.limit = limit;
        this.page = page;
        this.lists = lists;
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

    public List<PingListGetByIdResponse> getLists() {
        return lists;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PingListGetAllResponse that = (PingListGetAllResponse) o;
        return total == that.total &&
                limit == that.limit &&
                page == that.page &&
                lists.equals(that.lists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, limit, page, lists);
    }

    @Override
    public String toString() {
        return "BookGetAllResponse{" +
                "total=" + total +
                ", limit=" + limit +
                ", page=" + page +
                ", lists=" + lists +
                '}';
    }

}
