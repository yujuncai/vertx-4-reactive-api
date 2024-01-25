package org.limadelrey.vertx4.reactive.rest.api.protocol.vo;

import java.io.Serializable;

public class R   implements Serializable {

    private String code;
    private String msg;




    private  Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
