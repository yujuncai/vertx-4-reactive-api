package org.limadelrey.vertx4.reactive.rest.api.message;

import java.util.List;

public class CorsorMessage {

    private String pingId;

    private List<KeyVo> list;

    public List<KeyVo> getList() {
        return list;
    }

    public void setList(List<KeyVo> list) {
        this.list = list;
    }

    public String getPingId() {
        return pingId;
    }

    public void setPingId(String pingId) {
        this.pingId = pingId;
    }
}
