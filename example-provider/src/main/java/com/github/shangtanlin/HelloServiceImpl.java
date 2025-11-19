package com.github.shangtanlin;


import com.github.shangtanlin.annotation.RpcService;

@RpcService
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        // 模拟一点业务耗时
        System.out.println("服务端收到消息: " + name);
        return "Hello, " + name + "!";
    }
}