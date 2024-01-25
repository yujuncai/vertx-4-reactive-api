package org.limadelrey.vertx4.reactive.rest.api.protocol;


import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.ProtocolHeader;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.R;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.Req;

public class ProtocolContext {

    private  IProtocol protocol;

  public   ProtocolContext(IProtocol protocol){
      this.protocol=protocol;
  }


  public Req DecodeProtocol(byte[] buf){
     return protocol.DecodeProtocol( buf);
  }
}
