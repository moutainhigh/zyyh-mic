package com.jkys.zyyh.mic.core.dto;

import lombok.Data;

/**
 * 消息发送成功标识类.
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-16 10:31
 */
@Data
public class MsgSendSuccessFlag {
    /**
     * 消息是否发送成功
     */
    private boolean isSendSuccess;

    public MsgSendSuccessFlag(boolean isSendSuccess) {
        this.isSendSuccess = isSendSuccess;
    }
}
