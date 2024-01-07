package com.lhcygan.rpccommon.codec;

import com.lhcygan.rpccommon.serializer.CustomSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义编码器
 */
public class RpcEncoder extends MessageToByteEncoder {

    /**
     * 待编码的对象类型
     */
    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (genericClass.isInstance(o)) {
            // 将对象序列化为字节数组
            byte[] data = CustomSerializer.serialize(o);
            // 将消息体长度写入消息头
            byteBuf.writeInt(data.length);
            // 将数据写入消息体
            byteBuf.writeBytes(data);
        }
    }
}
