package com.sidiot.protocol;

import com.google.gson.Gson;
import com.sidiot.message.Message;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.msgpack.MessagePack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;



/**
 * 用于扩展序列化与反序列化算法
 * @author sidiot
 */
public interface Serializer {

    /**
     * 序列化方法
     */
    <T> byte[] serialize(T object);

    /**
     * 反序列化方法
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    enum Algorithm implements Serializer {
        Java {
            @Override
            public <T> byte[] serialize(T object) {
                System.out.println("序列化算法：Java");
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    return bos.toByteArray();
                } catch (Exception e) {
                    throw new RuntimeException("序列化失败：", e);
                }
            }

            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return (T) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("反序列化失败：", e);
                }
            }
        },

        JSON {
            @Override
            public <T> byte[] serialize(T object) {
                System.out.println("序列化算法：JSON");
                String json = new Gson().toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                String json = new String(bytes, StandardCharsets.UTF_8);
                return new Gson().fromJson(json, clazz);
            }
        },

        Protobuf {
            /**
             * 缓存 Schema
             */
            private Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

            @Override
            @SuppressWarnings("unchecked")
            public <T> byte[] serialize(T object) {
                System.out.println("序列化算法：Protobuf");
                Class<T> clazz = (Class<T>) object.getClass();
                Schema<T> schema = getSchema(clazz);
                LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
                byte[] bytes;
                try {
                    bytes = ProtostuffIOUtil.toByteArray(object, schema, buffer);
                } finally {
                    buffer.clear();
                }
                return bytes;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                Schema<T> schema = getSchema(clazz);
                T object = schema.newMessage();
                ProtostuffIOUtil.mergeFrom(bytes, object, schema);
                return object;
            }

            @SuppressWarnings("unchecked")
            private <T> Schema<T> getSchema(Class<T> clazz) {
                Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
                if (Objects.isNull(schema)) {
                    // 这个 schema 通过 RuntimeSchema 进行懒创建并缓存
                    // 所以可以一直调用 RuntimeSchema.getSchema()，这个方法是线程安全的
                    schema = RuntimeSchema.getSchema(clazz);
                    if (Objects.nonNull(schema)) {
                        schemaCache.put(clazz, schema);
                    }
                }
                return schema;
            }
        },

        MessagePack {
            @Override
            public <T> byte[] serialize(T object) {
                System.out.println("序列化算法：MessagePack");
                try {
                    return new MessagePack().write(object);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                try {
                    return new MessagePack().read(bytes, clazz);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
