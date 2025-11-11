package com.github.shangtanlin;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

public class RpcClient {

    /**
     * 获取一个服务的代理对象
     * @param serviceClass 接口类
     * @param host 服务主机
     * @param port 服务端口
     * @param <T> 泛型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> serviceClass, String host, int port) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass},
                new RpcInvocationHandler(host, port, serviceClass)
        );
    }

    /**
     * 动态代理的 InvocationHandler
     */
    private static class RpcInvocationHandler implements InvocationHandler {
        private final String host;
        private final int port;
        private final Class<?> serviceClass;

        public RpcInvocationHandler(String host, int port, Class<?> serviceClass) {
            this.host = host;
            this.port = port;
            this.serviceClass = serviceClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 1. 创建 RpcRequest 对象
            RpcRequest request = new RpcRequest();
            request.setInterfaceName(serviceClass.getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);

            System.out.println("客户端代理发起调用: " + request);

            // 2. 发起网络请求 (Socket + IO流)
            // try-with-resources 会自动关闭流和Socket
            try (Socket socket = new Socket(host, port);
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                // 3. 发送 RpcRequest
                oos.writeObject(request);
                oos.flush();

                // 4. 接收 RpcResponse
                RpcResponse response = (RpcResponse) ois.readObject();

                // 5. 处理响应
                if (response.hasException()) {
                    throw response.getException(); // 如果服务端有异常，则抛出
                } else {
                    return response.getResult(); // 返回正常结果
                }
            } catch (Exception e) {
                System.err.println("RPC 客户端调用失败: " + e.getMessage());
                throw new RuntimeException("RPC call failed", e);
            }
        }
    }
}