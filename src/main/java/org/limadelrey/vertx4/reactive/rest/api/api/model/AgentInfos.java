package org.limadelrey.vertx4.reactive.rest.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class AgentInfos implements Serializable {

    @Serial
    private static final long serialVersionUID = 1169010391380979103L;

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

    @JsonProperty(value = "action")
    private String action;
}
