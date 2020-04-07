package com.jkys.zyyh.mic.core.service.impl;

import com.jkys.agilegson.AGSON;
import com.jkys.zyyh.mic.core.dto.MicHisBizMessage;
import com.jkys.zyyh.mic.core.service.MsgProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息处理器抽象类，所有处理器都需要继承它来实现.
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-06 15:27
 */
@Slf4j
public abstract class BaseMsgProcessor implements MsgProcessor {


    @Override
    public boolean process(MicHisBizMessage micHisBizMessage) {
        boolean processed = false;
        if (match(micHisBizMessage)) {
            try {
                processed = true;
                doProcess(micHisBizMessage);
            } catch (Exception e) {
                log.warn("Process msg error. msg: [{}]", e, AGSON.toJSONString(micHisBizMessage));
            }
        }
        return processed;
    }

    /**
     * 是否匹配，匹配才对消息进行处理
     *
     * @param msg
     * @return
     */
    public abstract boolean match(MicHisBizMessage msg);

    /**
     * 对消息进行处理
     *
     * @param msg
     */
    public abstract void doProcess(MicHisBizMessage msg);

}
