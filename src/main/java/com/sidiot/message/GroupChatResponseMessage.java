package com.sidiot.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author sidiot
 */
@Data
@ToString(callSuper = true)
public class GroupChatResponseMessage extends AbstractResponseMessage {
    private String from;
    private String content;
    private String groupName;

    public GroupChatResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    public GroupChatResponseMessage(String from, String groupName, String content) {
        this.content = content;
        this.groupName = groupName;
        this.from = from;
    }

    @Override
    public int getMessageType() {
        return GROUP_CHAT_RESPONSE_MESSAGE;
    }
}
