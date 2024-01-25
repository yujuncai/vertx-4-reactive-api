package org.limadelrey.vertx4.reactive.rest.api;

import cn.hutool.json.JSONUtil;
import com.google.common.primitives.Bytes;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.R;
import org.limadelrey.vertx4.reactive.rest.api.protocol.vo.Req;
import org.limadelrey.vertx4.reactive.rest.api.utils.BytesUtils;

import java.io.*;
import java.net.Socket;

public class TcpClient {

    public static void main(String[] args) throws Exception {
        TcpClient client = new TcpClient();
        client.sendAndReceive();


    }


//    魔数，用来在第一时间判定是否是无效数据包，如java的coffee baby   1
//    版本号，可以支持协议的升级  1
//    序列化算法，消息正文到底采用哪种序列化反序列化方式，可以由此扩展，例如：json、protobuf、hessian、jdk  1
//    指令类型， 跟业务相关 1
//    请求序号，为了双工通信，提供异步能力 4
//    保留字段       4
//    正文长度 4
//    消息正文:如json,xml

    private void sendAndReceive() throws Exception {
        Socket client = new Socket("127.0.0.1", 9999);
        OutputStream out = client.getOutputStream();

        int i = 0;
        while (true){
            byte magic =0x1A ;
            byte version =0x01;
            byte serializerType =0x01 ;
            byte messageType =0x01 ;//0x01 心跳  0x02业务
            int sequenceId =54321 ;
            int extend =12345 ;

            out.write(magic); //写头
            out.write(version); //写头
            out.write(serializerType); //写头
            out.write(messageType); //写头

            byte[] sequenceIdBytLen = BytesUtils.int2BytesBig(sequenceId);
            byte[] extendBytLen = BytesUtils.int2BytesBig(extend);
            out.write(sequenceIdBytLen); //写头
            out.write(extendBytLen); //写头

            Req v=new Req();
            v.setToken("aaaaaaaaaaaaaaaaaaaaaaaaaaa");

            String data =  JSONUtil.toJsonPrettyStr(v);
            int len = data.getBytes().length;
            byte[] bytLen = BytesUtils.int2BytesBig(len);
            out.write(bytLen); //长度
            out.write(data.getBytes());
            out.flush();
            System.out.println(++i + "=====================发送结束！"+"报文----"+magic+" "+version+" "+serializerType+" "+messageType+" "+sequenceId+"  "+extend+" "+len+data);





            InputStream in = client.getInputStream();
             byte b[]=new byte[128];
             in.read(b);
             String s=new String(b);
            System.out.println("服务器返回报文："+s);
            Thread.sleep(2000);
        }


    }



}
