package com.github.shangtanlin;

/**
 * Hello world!
 *
 */
public class ClientMain {
    public static void main(String[] args) {
        RpcClient client = new RpcClient();

        // 1. 获取 HelloService 的代理对象
        // 注意：这里硬编码了地址和端口
        HelloService helloService = client.getProxy(HelloService.class, "127.0.0.1", 8080);

        // 2. 像调用本地方法一样调用 RPC
        String result = helloService.hello("RPC");
        System.out.println("客户端收到响应: " + result);

        // 3. 再试一次
        String result2 = helloService.hello("World");
        System.out.println("客户端收到响应: " + result2);
    }
}