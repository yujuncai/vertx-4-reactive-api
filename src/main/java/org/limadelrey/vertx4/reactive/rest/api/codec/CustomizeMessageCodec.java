package org.limadelrey.vertx4.reactive.rest.api.codec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import org.limadelrey.vertx4.reactive.rest.api.message.CorsorMessage;

import java.io.*;

public class CustomizeMessageCodec implements MessageCodec<CorsorMessage, CorsorMessage> {
    /**
     * 将消息实体封装到Buffer用于传输
     * 实现方式：使用对象流从对象中获取Byte数组然后追加到Buffer
     */
    @Override
    public void encodeToWire(Buffer buffer, CorsorMessage orderMessage) {
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        try (ObjectOutputStream o = new ObjectOutputStream(b)){
            o.writeObject(orderMessage);
            o.close();
            buffer.appendBytes(b.toByteArray());
        } catch (IOException e) { e.printStackTrace(); }
    }
    //从Buffer中获取消息对象
    @Override
    public CorsorMessage decodeFromWire(int pos, Buffer buffer) {
        final ByteArrayInputStream b = new ByteArrayInputStream(buffer.getBytes());
        CorsorMessage msg = null;
        try (ObjectInputStream o = new ObjectInputStream(b)){ msg = (CorsorMessage) o.readObject();
        } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
        return msg;
    }
    //消息转换
    @Override
    public CorsorMessage transform(CorsorMessage orderMessage) {
      //  System.out.println("消息转换---");//可对接受消息进行转换,比如转换成另一个对象等

        return orderMessage;
    }
    @Override
    public String name() { return "myCodec"; }
    //识别是否是用户自定义编解码器,通常为-1
    @Override
    public byte systemCodecID() { return -1; }
    public static MessageCodec create() {
        return new CustomizeMessageCodec();
    }
}