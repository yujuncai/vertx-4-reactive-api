package org.limadelrey.vertx4.reactive.rest.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Roles implements Serializable {

    @Serial
    private static final long serialVersionUID = 1169010391380979103L;

    @JsonProperty(value = "id")
    private  String id;

    @JsonProperty(value = "role_name")
    private String roleName;

    @JsonProperty(value = "role_prompt")
    private String rolePrompt;
}
