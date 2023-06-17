package com.sidiot.message;

import com.rpc.message.RpcRequestMessage;
import com.rpc.message.RpcResponseMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sidiot
 */
@Data
public abstract class Message implements Serializable {

    public static Class<?> getMessageClass(int messageType) {
        return MESSAGE_CLASSES.get(messageType);
    }

    private int sequenceId;

    private int messageType;

    public abstract int getMessageType();

    public static final int LOGIN_REQUEST_MESSAGE = 0;
    public static final int LOGIN_RESPONSE_MESSAGE = 1;
    public static final int CHAT_REQUEST_MESSAGE = 2;
    public static final int CHAT_RESPONSE_MESSAGE = 3;
    public static final int GROUP_CREATE_REQUEST_MESSAGE = 4;
    public static final int GROUP_CREATE_RESPONSE_MESSAGE = 5;
    public static final int GROUP_JOIN_REQUEST_MESSAGE = 6;
    public static final int GROUP_JOIN_RESPONSE_MESSAGE = 7;
    public static final int GROUP_QUIT_REQUEST_MESSAGE = 8;
    public static final int GROUP_QUIT_RESPONSE_MESSAGE = 9;
    public static final int GROUP_CHAT_REQUEST_MESSAGE = 10;
    public static final int GROUP_CHAT_RESPONSE_MESSAGE = 11;
    public static final int GROUP_MEMBERS_REQUEST_MESSAGE = 12;
    public static final int GROUP_MEMBERS_RESPONSE_MESSAGE = 13;

    public static final int PING_MESSAGE = 14;
    public static final int PONG_MESSAGE = 15;

    public static final int RPC_MESSAGE_TYPE_REQUEST = 101;
    public static final int  RPC_MESSAGE_TYPE_RESPONSE = 102;

    private static final Map<Integer, Class<?>> MESSAGE_CLASSES = new HashMap<>();

    static {
        MESSAGE_CLASSES.put(LOGIN_REQUEST_MESSAGE, LoginRequestMessage.class);
        MESSAGE_CLASSES.put(LOGIN_RESPONSE_MESSAGE, LoginResponseMessage.class);
        MESSAGE_CLASSES.put(CHAT_REQUEST_MESSAGE, ChatRequestMessage.class);
        MESSAGE_CLASSES.put(CHAT_RESPONSE_MESSAGE, ChatResponseMessage.class);
        MESSAGE_CLASSES.put(GROUP_CREATE_REQUEST_MESSAGE, GroupCreateRequestMessage.class);
        MESSAGE_CLASSES.put(GROUP_CREATE_RESPONSE_MESSAGE, GroupCreateResponseMessage.class);
        MESSAGE_CLASSES.put(GROUP_JOIN_REQUEST_MESSAGE, GroupJoinRequestMessage.class);
        MESSAGE_CLASSES.put(GROUP_JOIN_RESPONSE_MESSAGE, GroupJoinResponseMessage.class);
        MESSAGE_CLASSES.put(GROUP_QUIT_REQUEST_MESSAGE, GroupQuitRequestMessage.class);
        MESSAGE_CLASSES.put(GROUP_QUIT_RESPONSE_MESSAGE, GroupQuitResponseMessage.class);
        MESSAGE_CLASSES.put(GROUP_CHAT_REQUEST_MESSAGE, GroupChatRequestMessage.class);
        MESSAGE_CLASSES.put(GROUP_CHAT_RESPONSE_MESSAGE, GroupChatResponseMessage.class);
        MESSAGE_CLASSES.put(GROUP_MEMBERS_REQUEST_MESSAGE, GroupMembersRequestMessage.class);
        MESSAGE_CLASSES.put(GROUP_MEMBERS_RESPONSE_MESSAGE, GroupMembersResponseMessage.class);

        MESSAGE_CLASSES.put(RPC_MESSAGE_TYPE_REQUEST, RpcRequestMessage.class);
        MESSAGE_CLASSES.put(RPC_MESSAGE_TYPE_RESPONSE, RpcResponseMessage.class);
    }
}
