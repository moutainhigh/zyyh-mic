package com.jkys.zyyh.mic.core.service.impl;

import com.jkys.agilegson.AGSON;
import com.jkys.hhc.conn.model.WardModel;
import com.jkys.zyyh.mic.api.dto.SimpleHisMessageContext;
import com.jkys.zyyh.mic.api.service.WardMsgParser;
import com.jkys.zyyh.mic.common.common.MicMessageBizType;
import com.jkys.zyyh.mic.core.config.MicConfig;
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

import java.util.Objects;

/**
 * 病区消息处理器.
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-17 10:07
 */
@Slf4j
@Order(6)
@Service
public class WardMsgProcessor extends BaseMsgProcessor {
    @Autowired
    private MicConfig micConfig;
    @Autowired
    private MsgSendService msgSendService;
    @Autowired
    private MicMsgPersistenceService micMsgPersistenceService;
    @Autowired
    private MicMsgMapper micMsgMapper;

    /**
     * 可扩展病区消息解析器
     */
    private WardMsgParser wardMsgParser = ServiceOrchestrator.getOrchestrator(WardMsgParser.class);

    @Override
    public boolean match(MicHisBizMessage msg) {
        return MicMessageBizType.WARD.equals(msg.getMicBizType());
    }

    @Override
    public void doProcess(MicHisBizMessage msg) {
        log.debug("submit ward message msg identify: [{}]", msg.getMsgIdentify());
        // 解析病区消息
        SimpleHisMessageContext msgContext = new SimpleHisMessageContext();
        msgContext.setHospitalId(micConfig.getHospitalId());
        msgContext.setHisBizMessage(msg);
        WardModel wardModel = wardMsgParser.apply(msgContext);

        if (log.isDebugEnabled()) {
            log.debug("msg model json [{}]", AGSON.toJSONString(wardModel));
        }

        // 发送病区消息
        MsgSendSuccessFlag msgSendSuccessFlag = msgSendService.sendMsg(msg.getMicBizType(), msg.getMicBizEvent(), wardModel);
        if (Objects.equals(msgSendSuccessFlag.isSendSuccess(), true)) {
            log.debug("ward message send success");
            // 更新消息发送状态
            micMsgMapper.setMsgConsumed(msg.getId());
        } else {
            //更新重试次数
            micMsgPersistenceService.updateRetryCount(msg.getId());
        }
    }
}
