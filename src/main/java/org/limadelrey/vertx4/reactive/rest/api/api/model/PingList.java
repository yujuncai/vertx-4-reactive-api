package org.limadelrey.vertx4.reactive.rest.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PingList implements Serializable {

    @Serial
    private static final long serialVersionUID = 1169010391380979103L;

    @JsonProperty(value = "ping_id")
    private  String pingId;

    @JsonProperty(value = "role_id")
    private String roleId;

    @JsonProperty(value = "source_id")
    private String sourceId;

    @JsonProperty(value = "target_id")
    private String targetId;


    @JsonProperty(value = "desc_info")
    private String descInfo;
}
