package com.github.shangtanlin;

/**
 * Hello world!
 *
 */
public class ServerMain {
    public static void main(String[] args) {
        try {
            RpcServer server = new RpcServer();

            // 1. 注册我们的 HelloService
            server.register(new HelloServiceImpl());

            // 2. 启动服务器，监听 8080 端口
            server.start(8080);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}