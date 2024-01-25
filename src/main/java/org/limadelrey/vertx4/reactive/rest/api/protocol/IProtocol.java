package org.limadelrey.vertx4.reactive.rest.api.protocol;


import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.ProtocolHeader;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.Req;

public interface IProtocol {


    public Req DecodeProtocol(byte[] buf);

}
