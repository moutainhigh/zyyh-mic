package com.jkys.zyyh.mic.core.dto;

import com.jkys.zyyh.mic.api.common.HisBizMessage;
import com.jkys.zyyh.mic.dao.entity.MicMsg;

/**
 * mic 项目的HisBizMessage实现
 * @author twj
 * 2020/1/7 18:23
 */
public class MicHisBizMessage implements HisBizMessage {

    private Long id;
    //消息标识
    private String msgIdentify;
    private String content;
    /**
     * 消息业务类型
     */
    private String micBizType;
    /**
     * 消息的业务事件
     */
    private String micBizEvent;

    public MicHisBizMessage() {
    }

    public MicHisBizMessage(MicMsg micMsg) {
        this.id = micMsg.getId();
        this.content = micMsg.getContent();
        this.micBizType = micMsg.getMicBizType();
        this.micBizEvent = micMsg.getMicBizEvent();
        this.msgIdentify = micMsg.getMsgIdentify();
    }

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

    /**
     * mic 的消息id
     * @return
     */
    public Long getId() {
        return id;
    }

    public String getMsgIdentify() {
        return msgIdentify;
    }
}
