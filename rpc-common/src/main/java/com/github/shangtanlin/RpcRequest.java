package com.github.shangtanlin;

import java.io.Serializable;
import java.util.Arrays;

/**
 * RPC 请求对象
 * 必须实现 Serializable 接口才能在网络中传输
 */
//不再需要实现Serializable接口了，Kryo不需要
public class RpcRequest   {
    private int requestId; // 新增请求ID

    private String interfaceName;  // 接口名
    private String methodName;     // 方法名
    private Class<?>[] parameterTypes; // 参数类型
    private Object[] parameters;     // 参数


// --- Getters and Setters ---

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {}

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "interfaceName='" + interfaceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}