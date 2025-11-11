package com.github.shangtanlin;

public interface HelloService {
    /**
     * RPC 方法
     * @param name 名字
     * @return "Hello, [name]!"
     */
    String hello(String name);
}