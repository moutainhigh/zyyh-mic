package com.jkys.zyyh.mic.core.dto;

import lombok.Data;

/**
 * 发送至hospital项目的消息实体.
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-16 16:42
 */
@Data
public class HospitalMessage<T> {

    /**
     * 消息业务类型
     */
    private String micBizType;
    /**
     * 消息的业务事件
     */
    private String micBizEvent;

    /**
     * 消息Model
     */
    private T msgModel;

}
