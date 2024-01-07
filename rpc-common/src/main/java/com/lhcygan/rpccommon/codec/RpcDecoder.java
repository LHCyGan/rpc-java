package com.lhcygan.rpccommon.codec;

import com.lhcygan.rpccommon.serializer.CustomSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 自定义解码器
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 消息头占 4B，所以 "入站"数据（待解码的字节序列）的可读字节必须大于 4
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        // 标记当前readIndex的位置，以便后面重置 readIndex 的时候使用
        byteBuf.markReaderIndex();
        // 读取消息体（消息的长度）. readInt 操作会增加 readerIndex
        int dataLength = byteBuf.readInt();
        // 如果可读字节数小于消息长度，说明是不完整的消息
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }
        // 开始反序列化
        byte[] body = new byte[dataLength];
        byteBuf.readBytes(body);
        Object obj = CustomSerializer.deserialize(body, genericClass);
        list.add(obj);
    }
}
