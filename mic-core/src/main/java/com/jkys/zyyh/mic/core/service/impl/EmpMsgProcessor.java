package com.jkys.zyyh.mic.core.service.impl;

import com.jkys.agilegson.AGSON;
import com.jkys.hhc.conn.model.EmployeeModel;
import com.jkys.zyyh.mic.api.dto.SimpleHisMessageContext;
import com.jkys.zyyh.mic.api.service.EmpMsgParser;
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
 * 医护消息处理器.
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-06 16:01
 */
@Slf4j
@Order(3)
@Service
public class EmpMsgProcessor extends BaseMsgProcessor {
    @Autowired
    private MicConfig micConfig;
    @Autowired
    private MsgSendService msgSendService;
    @Autowired
    private MicMsgPersistenceService micMsgPersistenceService;
    @Autowired
    private MicMsgMapper micMsgMapper;

    /**
     * 可扩展医护消息解析器
     */
    private EmpMsgParser empMsgParser = ServiceOrchestrator.getOrchestrator(EmpMsgParser.class);

    @Override
    public boolean match(MicHisBizMessage msg) {
        return MicMessageBizType.EMP.equals(msg.getMicBizType());
    }

    @Override
    public void doProcess(MicHisBizMessage msg) {
        log.debug("submit employee message msg identify: [{}]", msg.getMsgIdentify());
        // 解析医护消息
        SimpleHisMessageContext msgContext = new SimpleHisMessageContext();
        msgContext.setHospitalId(micConfig.getHospitalId());
        msgContext.setHisBizMessage(msg);
        EmployeeModel employeeModel = empMsgParser.apply(msgContext);

        if (log.isDebugEnabled()) {
            log.debug("msg model json [{}]", AGSON.toJSONString(employeeModel));
        }

        // 发送医护消息
        MsgSendSuccessFlag msgSendSuccessFlag = msgSendService.sendMsg(msg.getMicBizType(), msg.getMicBizEvent(), employeeModel);
        if (Objects.equals(msgSendSuccessFlag.isSendSuccess(), true)) {
            log.debug("employee message send success");
            // 更新消息发送状态
            micMsgMapper.setMsgConsumed(msg.getId());
        } else {
            //更新重试次数
            micMsgPersistenceService.updateRetryCount(msg.getId());
        }
    }
}
