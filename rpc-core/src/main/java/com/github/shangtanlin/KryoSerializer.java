package com.github.shangtanlin;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

// 注意：Kryo 不是线程安全的，所以我们使用 ThreadLocal
public class KryoSerializer implements Serializer {
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 注册 RpcRequest 和 RpcResponse
        // 注意：Kryo 要求注册，否则性能会降低并且可能出错
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        // 注册 Java 的标准类
        kryo.register(Class.class);
        kryo.register(Class[].class);
        kryo.register(Object[].class);
        // ... 你可能需要注册更多，比如各种参数类型

        // 允许反序列化没有无参构造函数的类
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(
                new StdInstantiatorStrategy()));
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             Output output = new Output(bos)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            return output.toBytes();
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             Input input = new Input(bis)) {
            Kryo kryo = kryoThreadLocal.get();
            return kryo.readObject(input, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }
}
