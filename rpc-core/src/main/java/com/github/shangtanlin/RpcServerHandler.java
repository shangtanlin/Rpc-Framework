package com.github.shangtanlin;


// -----------------------------------------------------------------
// 需要一个单独的 Handler 类来处理业务逻辑
// -----------------------------------------------------------------

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ExecutorService;

// RpcServerHandler.java (可以作为 RpcServer 的内部类，或单独文件)
// 我们使用 SimpleChannelInboundHandler 来自动释放 RpcRequest
class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final Map<String, Object> serviceRegistry;
    private final ExecutorService businessThreadPool;

    public RpcServerHandler(Map<String, Object> serviceRegistry, ExecutorService businessThreadPool) {
        this.serviceRegistry = serviceRegistry;
        this.businessThreadPool = businessThreadPool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        // 从 RpcDecoder 我们知道 msg 已经是 RpcRequest 对象

        // 将耗时的业务逻辑（反射调用）丢到业务线程池，
        // 避免阻塞 Netty 的 I/O 线程
        businessThreadPool.submit(() -> {
            RpcResponse response = new RpcResponse();
            response.setRequestId(request.getRequestId()); // 关键！

            try {
                // 1. 找到服务
                Object service = serviceRegistry.get(request.getInterfaceName());
                if (service == null) {
                    throw new Exception("服务未找到: " + request.getInterfaceName());
                }

                // 2. 找到方法 (反射)
                Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterTypes());

                // 3. 执行方法 (反射)
                Object result = method.invoke(service, request.getParameters());
                response.setResult(result);

            } catch (Exception e) {
                System.err.println("方法调用失败: " + e.getMessage());
                response.setException(e.getCause() != null ? e.getCause() : e);
            }

            // 4. 将 RpcResponse 写回
            // Netty 会自动帮我们找到 RpcEncoder 进行编码
            ctx.writeAndFlush(response);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}