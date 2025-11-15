package com.github.shangtanlin;

import java.io.Serializable;

/**
 * RPC 响应对象
 * 必须实现 Serializable 接口
 */
public class RpcResponse  {
    private int requestId; // 新增请求ID

    private Object result;       // 方法执行结果
    private Throwable exception; // 异常信息

    public boolean hasException() {
        return exception != null;
    }

    // --- Getters and Setters ---


    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {}


    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "result=" + result +
                ", exception=" + exception +
                '}';
    }
}