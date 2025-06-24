package org.limadelrey.vertx4.reactive.rest.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RolesGetByIdResponse implements Serializable {

    private static final long serialVersionUID = 7621071075786169611L;

    @JsonProperty(value = "id")
    private  String id;

    @JsonProperty(value = "role_name")
    private String roleName;

    @JsonProperty(value = "role_prompt")
    private String rolePrompt;
    @JsonProperty(value = "role_value")
    private String roleValue;
    public RolesGetByIdResponse(Roles book) {
        this.id = book.getId();
        this.roleName = book.getRoleName();
        this.rolePrompt = book.getRolePrompt();
        this.roleValue = book.getRoleValue();
    }



}
