package com.github.shangtanlin;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {
    // 使用线程池处理并发
    private final ExecutorService threadPool;
    // 存储"服务名"到"服务实例"的映射
    private final Map<String, Object> serviceRegistry;

    public RpcServer() {
        this.threadPool = Executors.newCachedThreadPool();
        this.serviceRegistry = new HashMap<>();
    }

    /**
     * 注册一个服务实例
     * @param service 要注册的服务
     */
    public void register(Object service) {
        // 在MVP中，我们假设只实现了一个接口
        String interfaceName = service.getClass().getInterfaces()[0].getName();
        serviceRegistry.put(interfaceName, service);
        System.out.println("服务已注册: " + interfaceName);
    }

    /**
     * 启动服务器并监听端口
     * @param port 端口
     * @throws Exception 异常
     */
    public void start(int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("RPC 服务器已启动，监听端口: " + port);

        while (true) {
            Socket socket = serverSocket.accept(); // 阻塞，等待客户端连接
            System.out.println("客户端已连接: " + socket.getInetAddress());
            // 丢给线程池处理
            threadPool.submit(() -> handleRequest(socket));
        }
    }

    /**
     * 处理单个客户端请求
     * @param socket 客户端Socket
     */
    private void handleRequest(Socket socket) {
        // try-with-resources 自动关闭流
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

            // 1. 读取 RpcRequest
            RpcRequest request = (RpcRequest) ois.readObject();

            // 2. 查找服务并调用
            RpcResponse response = invoke(request);

            // 3. 写回 RpcResponse
            oos.writeObject(response);
            oos.flush();

        } catch (Exception e) {
            System.err.println("处理请求失败: " + e.getMessage());
        }
    }

    /**
     * 核心的反射调用方法
     * @param request RpcRequest
     * @return RpcResponse
     */
    private RpcResponse invoke(RpcRequest request) {
        RpcResponse response = new RpcResponse();
        try {
            // 1. 找到服务实例
            Object service = serviceRegistry.get(request.getInterfaceName());
            if (service == null) {
                throw new Exception("服务未找到: " + request.getInterfaceName());
            }

            // 2. 找到方法 (通过 反射)
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterTypes());

            // 3. 执行方法 (通过 反射)
            Object result = method.invoke(service, request.getParameters());

            response.setResult(result);
        } catch (Exception e) {
            System.err.println("方法调用失败: " + e);
            response.setException(e.getCause() != null ? e.getCause() : e);
        }
        return response;
    }
}
