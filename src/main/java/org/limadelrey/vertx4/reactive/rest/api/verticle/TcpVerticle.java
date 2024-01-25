package org.limadelrey.vertx4.reactive.rest.api.verticle;

import cn.hutool.json.JSONUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import io.vertx.ext.web.Router;
import org.limadelrey.vertx4.reactive.rest.api.protocol.ChooseProtocol;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.ProtocolHeader;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.R;
import org.limadelrey.vertx4.reactive.rest.api.utils.ConfigUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.RUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class TcpVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpVerticle.class);
    private static final String TCP_PORT = "tcp.port";

    @Override
    public void start(Promise<Void> promise) throws Exception {
        final Router router = Router.router(vertx);
        buildTcpServer(vertx,promise,router);

    }


//    魔数，用来在第一时间判定是否是无效数据包，如java的coffee baby   1
//    版本号，可以支持协议的升级  1
//    序列化算法，消息正文到底采用哪种序列化反序列化方式，可以由此扩展，例如：json、protobuf、hessian、jdk  1
//    指令类型， 跟业务相关 1
//    请求序号，为了双工通信，提供异步能力 4
//    保留字段       4
//    正文长度 4
//    消息正文:如json,xml



    private void buildTcpServer(Vertx vertx,
                                 Promise<Void> promise,
                                 Router router) {
        final Properties properties = ConfigUtils.getInstance().getProperties();
        final   Integer port=  Integer.parseInt(properties.getProperty(TCP_PORT));
        // 创建TCP Server
        int len_header = 16 ;
        NetServer server = vertx.createNetServer();
        // 设置Handler
        server.connectHandler(socket -> {
            // 构造parser
            RecordParser parser = RecordParser.newFixed(len_header);
            parser.setOutput(new Handler<Buffer>() {
                int size = -1;
                ProtocolHeader vo=new ProtocolHeader();
                public void handle(Buffer buffer) {
                    //找到头
                    if (-1 == size) {
                        byte magic = buffer.getByte(0); //第一个字节0x1A
                        if(magic != 0x1A){
                            System.out.println("----ignore----->"+magic);
                            return; //头，忽略
                        }
                        vo.setMagic(magic);
                        vo.setVersion( buffer.getByte(1));
                        vo.setSerializerType(buffer.getByte(2));
                        vo.setMessageType(buffer.getByte(3));
                        vo.setSequenceId(buffer.getInt(4));
                        vo.setExtend(buffer.getInt(8));
                        size = buffer.getInt(12);
                        parser.fixedSizeMode(size);
                    } else {
                        byte[] buf = buffer.getBytes();
                        try {
                            R choose = ChooseProtocol.chooseSerializer(vo, buf);//选择序列化方式
                            Buffer b = RUtils.buildSucessBuffer(choose);
                            socket.write(b);
                        } catch (Exception e) {
                            LOGGER.info(socket.writeHandlerID()+" data解析失败，强制close");
                            Buffer b = RUtils.buildFailBuffer();
                            socket.write(b);
                            socket.close();
                            return;
                        }
                        parser.fixedSizeMode(len_header);
                        size = -1;
                    }//end if-else
                }
            });

            socket.handler(parser);
            // 监听客户端的退出连接
            socket.closeHandler(close -> {
                System.out.println("客户端退出连接");
            });
           // socket.exceptionHandler();
        });

        // 监听
        server.listen(port, "127.0.0.1", res -> {
            if (res.succeeded()) {
                promise.complete();
                LOGGER.info("tcp server running on port "+port );
            } else {
                promise.fail(res.cause());
                LOGGER.info(res.cause().getMessage());
            }
        });
    }

}

