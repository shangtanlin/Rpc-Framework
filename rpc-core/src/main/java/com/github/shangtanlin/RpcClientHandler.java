package com.github.shangtanlin;

// -----------------------------------------------------------------
// RpcClientHandler 负责接收 RpcResponse 并完成对应的 Future
// -----------------------------------------------------------------

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.CompletableFuture;

class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        System.out.println("客户端收到响应: " + response);

        // 1. 从 PENDING_FUTURES 找到对应的 Future
        CompletableFuture<RpcResponse> future = RpcClient.PENDING_FUTURES.remove(response.getRequestId());

        if (future != null) {
            // 2. 设置结果，唤醒等待的 invoke() 方法
            future.complete(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}