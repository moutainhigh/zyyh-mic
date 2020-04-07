package com.jkys.zyyh.mic.core.service.impl;

import com.jkys.zyyh.mic.core.dto.HospitalMessage;
import com.jkys.zyyh.mic.core.dto.MsgSendSuccessFlag;
import com.jkys.zyyh.mic.core.service.HospitalMsgSubmitter;
import com.jkys.zyyh.mic.core.service.MsgSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author twj
 * 2020/1/21 10:49
 */
@Service
public class MsgSendServiceImpl implements MsgSendService {


    @Autowired
    @Qualifier("HttpHospitalMsgSubmitter")
    private HospitalMsgSubmitter hospitalMsgSubmitter;

    @Override
    public MsgSendSuccessFlag sendMsg(String micBizType, String micBizEvent, Object msgModel) {
        MsgSendSuccessFlag msgSendSuccessFlag = new MsgSendSuccessFlag(false);
        HospitalMessage hospitalMessage = new HospitalMessage();
        hospitalMessage.setMicBizType(micBizType);
        hospitalMessage.setMicBizEvent(micBizEvent);
        hospitalMessage.setMsgModel(msgModel);
        //可以简化
        hospitalMsgSubmitter.submit(msgSendSuccessFlag, hospitalMessage);
        return msgSendSuccessFlag;
    }
}
