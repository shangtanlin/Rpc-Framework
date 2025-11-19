package com.github.shangtanlin;

import com.github.shangtanlin.annotation.RpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    // <--- 魔法在这里！自动注入代理对象
    @RpcReference
    private HelloService helloService;

    @GetMapping("/hello")
    public String sayHello(@RequestParam(defaultValue = "User") String name) {
        // 像调用本地方法一样调用
        return helloService.hello(name);
    }
}