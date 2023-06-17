package com.sidiot.message;

/**
 * @author sidiot
 */
public class PingMessage extends Message {
    @Override
    public int getMessageType() {
        return PING_MESSAGE;
    }
}
