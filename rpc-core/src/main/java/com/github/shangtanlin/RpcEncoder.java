package com.github.shangtanlin;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

// 编码器：将 RpcRequest/RpcResponse 对象 -> 编码为 -> ByteBuf
public class RpcEncoder extends MessageToByteEncoder<Object> {

    // 假设我们现在只有 Kryo 序列化
    private Serializer serializer = new KryoSerializer();

    // 魔数
    private static final byte[] MAGIC_NUMBER = new byte[]{(byte) 'r', (byte) 'p', (byte) 'c', (byte) '!'};

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 1. 写魔数 (4 bytes)
        out.writeBytes(MAGIC_NUMBER);

        // 2. 写版本 (1 byte)
        out.writeByte(1);

        // 3. 写序列化方式 (1 byte) - 1:Kryo
        out.writeByte(1);

        // 4. 写消息类型 (1 byte)
        byte messageType;
        if (msg instanceof RpcRequest) {
            messageType = 1;
        } else if (msg instanceof RpcResponse) {
            messageType = 2;
        } else {
            // (心跳等... 暂不处理)
            messageType = 0;
        }
        out.writeByte(messageType);

        // 5. 写请求ID (4 bytes)
        int requestId = 0;
        if (msg instanceof RpcRequest) {
            requestId = ((RpcRequest) msg).getRequestId();
        } else if (msg instanceof RpcResponse) {
            requestId = ((RpcResponse) msg).getRequestId();
        }
        out.writeInt(requestId);

        // 6. 序列化数据体
        byte[] body = serializer.serialize(msg);

        // 7. 写数据长度 (4 bytes)
        out.writeInt(body.length);

        // 8. 写数据体 (N bytes)
        out.writeBytes(body);
    }
}