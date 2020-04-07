package com.jkys.zyyh.mic.core.service;

import com.jkys.zyyh.mic.core.dto.MicHisBizMessage;

/**
 * 消息处理器.
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-06 15:21
 */
public interface MsgProcessor {

    /**
     * 处理消息
     *
     * @param micHisBizMessage 消息体
     * @return 返回处理的结果
     */
    boolean process(MicHisBizMessage micHisBizMessage);
}
