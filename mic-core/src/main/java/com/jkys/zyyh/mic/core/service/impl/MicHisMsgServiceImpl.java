package com.jkys.zyyh.mic.core.service.impl;

import com.jkys.zyyh.mic.api.common.HisBizMessage;
import com.jkys.zyyh.mic.api.service.MicHisMsgService;
import com.jkys.zyyh.mic.core.service.MicMsgPersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * MicHisMsgService 实现
 * @author twj
 * 2020/1/19 15:51
 */
@Service
@Slf4j
@Primary
public class MicHisMsgServiceImpl implements MicHisMsgService {
    @Autowired
    private MicMsgPersistenceService micMsgPersistenceService;

    @Override
    public void receiveMsg(HisBizMessage hisBizMessage) {
        micMsgPersistenceService.saveMsg(hisBizMessage);
    }
}
