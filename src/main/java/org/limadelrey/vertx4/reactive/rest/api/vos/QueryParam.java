package org.limadelrey.vertx4.reactive.rest.api.vos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class QueryParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1169010391380979103L;

    @JsonProperty(value = "rolesId")
    private  String rolesId;


    @JsonProperty(value = "sid")
    private  String sid;


    @JsonProperty(value = "tid")
    private  String tid;

    @JsonProperty(value = "loop")
    private  Integer loop;

    @JsonProperty(value = "desc")
    private  String desc;

}
