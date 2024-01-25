package org.limadelrey.vertx4.reactive.rest.api.protocol;

import cn.hutool.core.bean.BeanUtil;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.ProtocolHeader;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.R;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.Req;

public class ChooseProtocol {

    public static R chooseSerializer(ProtocolHeader vo, byte[] buf){
        R rr=new R();


        byte serializerType = vo.getSerializerType();//序列化协议
        if(serializerType==0x01){//json
          IProtocol   codec=new JsonCodec();
          ProtocolContext context=new ProtocolContext(codec);
          Req r = context.DecodeProtocol(buf);
          rr.setData(r);
        }else {

        }
        return  rr;
    }

}
