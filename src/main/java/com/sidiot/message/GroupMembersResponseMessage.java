package com.sidiot.message;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

/**
 * @author sidiot
 */
@Data
@ToString(callSuper = true)
public class GroupMembersResponseMessage extends Message {

    private Set<String> members;

    public GroupMembersResponseMessage(Set<String> members) {
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return GROUP_MEMBERS_RESPONSE_MESSAGE;
    }
}
