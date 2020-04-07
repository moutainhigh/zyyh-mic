package com.jkys.zyyh.mic.core.service;

import com.jkys.zyyh.mic.core.dto.HospitalMessage;
import com.jkys.zyyh.mic.core.dto.MsgSendSuccessFlag;

/**
 * 发送消息处理器.
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-13 15:42
 */
public interface HospitalMsgSubmitter {

    /**
     * 处理消息发送
     *
     * @param msgSendSuccessFlag 消息是否成功发送的包装类
     * @param hospitalMessage            消息体
     * @return
     */
    boolean submit(MsgSendSuccessFlag msgSendSuccessFlag, HospitalMessage hospitalMessage);

}
