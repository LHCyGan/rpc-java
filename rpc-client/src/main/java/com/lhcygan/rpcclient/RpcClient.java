package com.lhcygan.rpcclient;

import com.lhcygan.rpccommon.codec.RpcDecoder;
import com.lhcygan.rpccommon.codec.RpcEncoder;
import com.lhcygan.rpccommon.model.entity.RpcRequest;
import com.lhcygan.rpccommon.model.entity.RpcResponse;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.tomcat.util.net.NioChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RPC 客户端（建立连接，发送 RPC 请求，接收 RPC 响应）
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger log = LoggerFactory.getLogger(RpcClient.class);

    private final String host;
    private final int port;

    // RPC 响应
    private RpcResponse response;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 处理服务端发送过来的响应消息 RpcResponse
     * @param channelHandlerContext
     * @param rpcResponse
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.response = rpcResponse;
    }

    /**
     * 发生异常时此方法被调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("api caught exception", cause);
        ctx.close();
    }

    /**
     * 建立连接，发送请求，接收响应
     * 该方法的真正调用在代理类 RpcProxy 中（通过代理对此方法进行增强，屏蔽远程方法调用的细节）
     * @param rpcRequest
     * @return
     */
    public RpcResponse send(RpcRequest rpcRequest) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new RpcEncoder(RpcRequest.class));
                    pipeline.addLast(new RpcDecoder(RpcResponse.class));
                    pipeline.addLast(RpcClient.this);
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 连接 RPC 服务器
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            // 写入 RPC 请求
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(rpcRequest).sync();
            // 关闭连接
            channel.closeFuture().sync();
            // 返回 RPC 响应对象
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }
}
