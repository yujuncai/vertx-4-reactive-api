package org.limadelrey.vertx4.reactive.rest.api.utils;

import cn.hutool.json.JSONUtil;
import io.vertx.core.buffer.Buffer;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.R;

public class RUtils {

    public static  Buffer buildSucessBuffer(R r){
        r.setCode("00");
        r.setMsg("ok");
        Buffer buffer = Buffer.buffer();
        String data =  JSONUtil.toJsonPrettyStr( r);
        buffer.appendBytes(data.getBytes());
        return  buffer;

    }


    public static  Buffer buildFailBuffer(){
        R r=new R();
        r.setCode("99");
        r.setMsg("fail");
        Buffer buffer = Buffer.buffer();
        String data =  JSONUtil.toJsonPrettyStr( r);
        buffer.appendBytes(data.getBytes());
        return  buffer;

    }
    public static  Buffer buildFailBuffer(String code,String msg){
        R r=new R();
        r.setCode(code);
        r.setMsg(msg);
        Buffer buffer = Buffer.buffer();
        String data =  JSONUtil.toJsonPrettyStr( r);
        buffer.appendBytes(data.getBytes());
        return  buffer;

    }
}
