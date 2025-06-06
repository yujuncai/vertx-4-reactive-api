package org.limadelrey.vertx4.reactive.rest.api.vos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Builder
@Data
public class AnswerParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1169010391380979103L;

    @JsonProperty(value = "answer")
    private  String answer;



}
