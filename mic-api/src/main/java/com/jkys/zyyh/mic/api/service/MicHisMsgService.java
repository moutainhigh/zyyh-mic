package com.jkys.zyyh.mic.api.service;

import com.jkys.zyyh.mic.api.common.HisBizMessage;

/**
 * 提供二方包调用
 * his消息服务service
 * @author twj
 * 2020/1/19 15:50
 */
public interface MicHisMsgService {

    /**
     * 接受一条his消息
     * @param hisBizMessage
     */
    void receiveMsg(HisBizMessage hisBizMessage);
}
