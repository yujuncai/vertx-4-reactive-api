package org.limadelrey.vertx4.reactive.rest.api;

import cn.hutool.json.JSONUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.R;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.Req;
import org.limadelrey.vertx4.reactive.rest.api.utils.BytesUtils;

public class VertxTcpClient extends AbstractVerticle {
    private static NetSocket netSocket;

    @Override
    public void start() throws Exception {
        NetClient netClient = vertx.createNetClient();
        netClient.connect(9999, "127.0.0.1", connect -> {
            if (connect.succeeded()) {
                System.out.println("连接建立成功，开始发送数据！");
                netSocket = connect.result();
                byte magic =0x1A ;
                byte version =0x01;
                byte serializerType =0x01 ;
                byte messageType =1 ;
                int sequenceId =54321 ;
                int extend =12345 ;
                byte[] sequenceIdBytLen = BytesUtils.int2BytesBig(sequenceId);
                byte[] extendBytLen = BytesUtils.int2BytesBig(extend);
                Req v=new Req();
                v.setToken("aaaaaaaaaaaaaaaaaaaaaaaaaaa");
                String data =  JSONUtil.toJsonPrettyStr(v);
                int len = data.getBytes().length;
                byte[] bytLen = BytesUtils.int2BytesBig(len);
                byte[] b=new byte[1024];
                b[0]=magic;
                b[0]=magic;
                b[0]=magic;
                b[0]=magic;
                Buffer.buffer();

                Buffer buffer = Buffer.buffer()
                        .appendByte(magic)
                        .appendByte(version)
                        .appendByte(serializerType)
                        .appendByte(messageType)
                        .appendBytes(sequenceIdBytLen)
                        .appendBytes(extendBytLen)
                        .appendBytes(bytLen)
                        .appendBytes(data.getBytes());
                netSocket.write(buffer);
                netSocket.handler(resp -> {
                    System.out.println("接收到的数据为：" + resp.toString());
                });
            } else {
                System.out.println("服务器连接异常");
            }
        });
    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new VertxTcpClient());
    }
}
