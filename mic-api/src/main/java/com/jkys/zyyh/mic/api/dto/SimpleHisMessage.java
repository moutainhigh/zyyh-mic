package com.jkys.zyyh.mic.api.dto;

import com.jkys.zyyh.mic.api.common.HisBizMessage;

/**
 * 简单业务消息
 * @author twj
 * 2020/1/2 17:18
 */
public class SimpleHisMessage implements HisBizMessage {

    //消息文本
    private String content;
    //消息业务类型
    private String micBizType;
    //消息的业务事件
    private String micBizEvent;

    @Override
    public String getMicBizType() {
        return micBizType;
    }

    @Override
    public String getMicBizEvent() {
        return micBizEvent;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMicBizType(String micBizType) {
        this.micBizType = micBizType;
    }

    public void setMicBizEvent(String micBizEvent) {
        this.micBizEvent = micBizEvent;
    }
}
