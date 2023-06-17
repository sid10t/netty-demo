package com.sidiot.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author sidiot
 */
@Data
@ToString(callSuper = true)
public class GroupMembersRequestMessage extends Message {
    private String groupName;

    public GroupMembersRequestMessage(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int getMessageType() {
        return GROUP_MEMBERS_REQUEST_MESSAGE;
    }
}
