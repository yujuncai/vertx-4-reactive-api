package org.limadelrey.vertx4.reactive.rest.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class UserGetByIdResponse implements Serializable {

    private static final long serialVersionUID = 7621071075786169612L;

    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "user_name")
    private String userName;

    @JsonProperty(value = "password")
    private String password;

    @JsonProperty(value = "create_time")
    private Date createTime;

    @JsonProperty(value = "user_status")
    private Integer userStatus;

    public UserGetByIdResponse(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.createTime = user.getCreateTime();
        this.userStatus = user.getUserStatus();

    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Integer getUserStatus() {
        return userStatus;
    }
}