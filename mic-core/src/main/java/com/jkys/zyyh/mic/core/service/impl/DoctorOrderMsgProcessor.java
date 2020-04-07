package com.jkys.zyyh.mic.core.service.impl;

import com.jkys.agilegson.AGSON;
import com.jkys.hhc.conn.model.DoctorOrderModel;
import com.jkys.zyyh.mic.api.dto.SimpleHisMessageContext;
import com.jkys.zyyh.mic.api.service.DoctorOrderListParser;
import com.jkys.zyyh.mic.api.service.DoctorOrderParser;
import com.jkys.zyyh.mic.core.config.MicConfig;
import com.jkys.zyyh.mic.common.common.MicMessageBizType;
import com.jkys.zyyh.mic.core.dto.MicHisBizMessage;
import com.jkys.zyyh.mic.core.dto.MsgSendSuccessFlag;
import com.jkys.zyyh.mic.core.service.MicMsgPersistenceService;
import com.jkys.zyyh.mic.core.service.MsgSendService;
import com.jkys.zyyh.mic.dao.mapper.MicMsgMapper;
import com.jkys.zyyh.penida.core.ServiceOrchestrator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


/**
 * 医嘱消息处理器.
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-06 16:02
 */
@Order(1)
@Service
@Slf4j
public class DoctorOrderMsgProcessor extends BaseMsgProcessor {

    @Autowired
    private MicConfig micConfig;
    @Autowired
    private MsgSendService msgSendService;
    @Autowired
    private MicMsgPersistenceService micMsgPersistenceService;
    @Autowired
    private MicMsgMapper micMsgMapper;


    // private DoctorOrderParser doctorOrderParser = ServiceOrchestrator.getOrchestrator(DoctorOrderParser.class);

    private DoctorOrderListParser doctorOrderParser = ServiceOrchestrator.getOrchestrator(DoctorOrderListParser.class);

    @Override
    public boolean match(MicHisBizMessage msg) {
        return MicMessageBizType.DOCTOR_ORDER.equals(msg.getMicBizType());
    }

    @Override
    public void doProcess(MicHisBizMessage msg) {
        log.debug("submit doctorOrder message msg identify: [{}]", msg.getMsgIdentify());
        // 解析医嘱消息
        SimpleHisMessageContext msgContext = new SimpleHisMessageContext();
        msgContext.setHospitalId(micConfig.getHospitalId());
        msgContext.setHisBizMessage(msg);
        List<DoctorOrderModel> doctorOrderModels = doctorOrderParser.apply(msgContext);

        Boolean changeSuccessStatus = false;
        Boolean changeFailedStatus = false;

        for (DoctorOrderModel doctorOrderModel : doctorOrderModels) {
            log.info("msg model json [{}]", AGSON.toJSONString(doctorOrderModel));

            // 发送医嘱消息
            MsgSendSuccessFlag msgSendSuccessFlag = msgSendService.sendMsg(msg.getMicBizType(), msg.getMicBizEvent(), doctorOrderModel);
            if (Objects.equals(msgSendSuccessFlag.isSendSuccess(), true)) {
                log.info("doctorOrder message send success,orderNo [{}]",doctorOrderModel.getOrderNo());
                // 更新消息发送状态
                if (!changeSuccessStatus) {
                    micMsgMapper.setMsgConsumed(msg.getId());
                    changeSuccessStatus = true;
                }
            } else {
                log.warn("doctorOrder message send failed,orderNo [{}]",doctorOrderModel.getOrderNo());
                //更新重试次数
                if (!changeFailedStatus) {
                    micMsgPersistenceService.updateRetryCount(msg.getId());
                    changeFailedStatus = true;
                }
            }
        }
    }
}
