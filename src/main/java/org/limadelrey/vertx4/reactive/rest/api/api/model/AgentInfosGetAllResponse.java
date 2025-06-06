package org.limadelrey.vertx4.reactive.rest.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
@Data
public class AgentInfosGetAllResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -8964658883487451260L;

    @JsonProperty(value = "total")
    private final int total;

    @JsonProperty(value = "limit")
    private final int limit;

    @JsonProperty(value = "page")
    private final int page;

    @JsonProperty(value = "agents")
    private final List<AgentInosGetByIdResponse> agents;

    public AgentInfosGetAllResponse(int total,
                                    int limit,
                                    int page,
                                    List<AgentInosGetByIdResponse> agents) {
        this.total = total;
        this.limit = limit;
        this.page = page;
        this.agents = agents;
    }





}
