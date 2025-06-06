package org.limadelrey.vertx4.reactive.rest.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Book implements Serializable {

    private static final long serialVersionUID = 1169010391380979103L;

    @JsonProperty(value = "id")
    private int id;

    @JsonProperty(value = "querys")
    private String querys;

    @JsonProperty(value = "answer")
    private String answer;

    @JsonProperty(value = "agent_id")
    private String agentId;

    @JsonProperty(value = "type")
    private Integer type;

    @JsonProperty(value = "create_time")
    private Date createTime;



    @JsonProperty(value = "ping_id")
    private String pingId;




}
