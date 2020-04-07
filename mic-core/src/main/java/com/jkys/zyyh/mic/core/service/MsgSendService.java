package com.jkys.zyyh.mic.core.service;

import com.jkys.zyyh.mic.core.dto.MsgSendSuccessFlag;

/**
 * 消息发送院内服务
 *
 * @author twj
 * 2020/1/21 10:48
 */
public interface MsgSendService<T> {

    /**
     * 发送院内
     *
     * @param micBizType  消息类型
     * @param micBizEvent 消息事件
     * @param msgModel    解析后的消息Model
     * @return
     */
    MsgSendSuccessFlag sendMsg(String micBizType, String micBizEvent, T msgModel);
}
