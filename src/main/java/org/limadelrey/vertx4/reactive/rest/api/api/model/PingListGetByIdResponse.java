package org.limadelrey.vertx4.reactive.rest.api.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PingListGetByIdResponse implements Serializable {

    private static final long serialVersionUID = 7621071075786169611L;

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
    @JsonProperty(value = "status")
    private String status;


    @JsonProperty(value = "reports")
    private String reports;

    public PingListGetByIdResponse(PingList book) {
        this.pingId = book.getPingId();
        this.roleId = book.getRoleId();
        this.sourceId = book.getSourceId();
        this.targetId = book.getTargetId();
        this.descInfo = book.getDescInfo();
        this.status = book.getStatus();
        this.reports = book.getReports();
    }



}
