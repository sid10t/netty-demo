package com.sidiot.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author sidiot
 */
@Data
@ToString(callSuper = true)
public class GroupCreateResponseMessage extends AbstractResponseMessage {

    public GroupCreateResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return GROUP_CREATE_RESPONSE_MESSAGE;
    }
}
