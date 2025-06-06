package org.limadelrey.vertx4.reactive.rest.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class AgentInosGetByIdResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 7621071075786169612L;

    @JsonProperty(value = "id")
    private  String id;

    @JsonProperty(value = "hosts")
    private String hosts;

    @JsonProperty(value = "port")
    private Integer port;

    @JsonProperty(value = "uri")
    private String uri;

    @JsonProperty(value = "type")
    private String type;

    @JsonProperty(value = "apikey")
    private String apikey;


    public AgentInosGetByIdResponse(AgentInfos info) {
            this.id = info.getId();
            this.hosts = info.getHosts();
            this.port = info.getPort();
            this.uri = info.getUri();
            this.type = info.getType();
            this.apikey = info.getApikey();
    }


}
