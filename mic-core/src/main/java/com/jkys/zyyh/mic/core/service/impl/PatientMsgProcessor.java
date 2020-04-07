package com.jkys.zyyh.mic.core.service.impl;

import com.jkys.agilegson.AGSON;
import com.jkys.hhc.conn.model.PatientModel;
import com.jkys.zyyh.mic.api.dto.SimpleHisMessageContext;
import com.jkys.zyyh.mic.api.service.PatientMsgParser;
import com.jkys.zyyh.mic.core.config.MicConfig;
import com.jkys.zyyh.mic.core.dto.MicHisBizMessage;
import com.jkys.zyyh.mic.core.service.MicMsgPersistenceService;
import com.jkys.zyyh.mic.core.service.MsgSendService;
import com.jkys.zyyh.mic.common.common.MicMessageBizType;
import com.jkys.zyyh.mic.core.dto.MsgSendSuccessFlag;
import com.jkys.zyyh.mic.dao.mapper.MicMsgMapper;
import com.jkys.zyyh.penida.core.ServiceOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 病人消息处理器.
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-06 15:50
 */
@Slf4j
@Order(2)
@Service
public class PatientMsgProcessor extends BaseMsgProcessor {

    @Autowired
    private MicConfig micConfig;

    @Autowired
    private MsgSendService msgSendService;
    @Autowired
    private MicMsgPersistenceService micMsgPersistenceService;
    @Autowired
    private MicMsgMapper micMsgMapper;

    /**
     * 可扩展病人消息解析器
     */
    private PatientMsgParser patientMsgParser = ServiceOrchestrator.getOrchestrator(PatientMsgParser.class);

    @Override
    public boolean match(MicHisBizMessage msg) {
        return MicMessageBizType.PATIENT.equals(msg.getMicBizType());
    }

    @Override
    public void doProcess(MicHisBizMessage msg) {
        log.debug("submit patient message msg identify: [{}]", msg.getMsgIdentify());
        // 解析病人消息
        SimpleHisMessageContext msgContext = new SimpleHisMessageContext();
        msgContext.setHospitalId(micConfig.getHospitalId());
        msgContext.setHisBizMessage(msg);
        PatientModel patientModel = patientMsgParser.apply(msgContext);

        log.info("msg model json [{}]", AGSON.toJSONString(patientModel));

        // 发送病人消息
        MsgSendSuccessFlag msgSendSuccessFlag = msgSendService.sendMsg(msg.getMicBizType(), msg.getMicBizEvent(), patientModel);
        if (Objects.equals(msgSendSuccessFlag.isSendSuccess(), true)) {
            log.info("patient message send success,inPatientId [{}]",patientModel.getInpatientNo());
            // 更新消息发送状态
            micMsgMapper.setMsgConsumed(msg.getId());
        } else {
            log.warn("patient message send failed,inPatientId [{}]",patientModel.getInpatientNo());
            //更新重试次数
            micMsgPersistenceService.updateRetryCount(msg.getId());
        }

    }

}
