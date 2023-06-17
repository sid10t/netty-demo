package com.sidiot.message;

/**
 * @author sidiot
 */
public class PongMessage extends Message {
    @Override
    public int getMessageType() {
        return PONG_MESSAGE;
    }
}
