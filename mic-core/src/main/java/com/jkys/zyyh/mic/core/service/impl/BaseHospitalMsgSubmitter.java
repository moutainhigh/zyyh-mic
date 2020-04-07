package com.jkys.zyyh.mic.core.service.impl;

import com.jkys.agilegson.AGSON;
import com.jkys.zyyh.mic.core.dto.HospitalMessage;
import com.jkys.zyyh.mic.core.dto.MsgSendSuccessFlag;
import com.jkys.zyyh.mic.core.service.HospitalMsgSubmitter;
import lombok.extern.slf4j.Slf4j;

/**
 * 发送消息处理器抽象类,所有发送消息的处理器都需要继承它来实现.
 * <p>
 * 虽然目前只支持http的形式发送消息，考虑到后期可能需要支持
 * 多种发送方式，所以这里用策略模式做一个基类处理器，方便后期拓展。
 * </p>
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-14 14:07
 */
@Slf4j
public abstract class BaseHospitalMsgSubmitter implements HospitalMsgSubmitter {

    @Override
    public boolean submit(MsgSendSuccessFlag msgSendSuccessFlag, HospitalMessage hospitalMessage) {
        try {
            doSubmit(msgSendSuccessFlag, hospitalMessage);
        } catch (Exception e) {
            log.warn("submit msg error msg: [{}]", e, AGSON.toJSONString(hospitalMessage));
            return false;
        }

        return true;
    }

    /**
     * 对消息进行发送
     *
     * @param msgSendSuccessFlag 消息是否成功发送的包装类
     * @param hospitalMessage    消息体
     */
    public abstract void doSubmit(MsgSendSuccessFlag msgSendSuccessFlag, HospitalMessage hospitalMessage);

}
