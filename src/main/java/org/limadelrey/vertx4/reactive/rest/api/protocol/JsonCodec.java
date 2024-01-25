package org.limadelrey.vertx4.reactive.rest.api.protocol;

import cn.hutool.json.JSONUtil;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.ProtocolHeader;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.Req;

public class JsonCodec implements IProtocol{


    @Override
    public Req DecodeProtocol(byte[] buf) {

         String str = new String(buf);
       if(JSONUtil.isTypeJSON(str)){
           Req r = JSONUtil.toBean(str, Req.class);
            return r;
       }else{
           return null;
       }
    }
}
