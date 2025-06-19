package org.limadelrey.vertx4.reactive.rest.api.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
@Data
public class BookGetByIdResponse implements Serializable {

    private static final long serialVersionUID = 7621071075786169611L;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty(value = "create_time")
    private Instant createTime;

    public BookGetByIdResponse(Book book) {
        this.id = book.getId();
        this.querys = book.getQuerys();
        this.answer = book.getAnswer();
        this.agentId = book.getAgentId();
        this.type = book.getType();
        this.createTime = book.getCreateTime();
    }



}
