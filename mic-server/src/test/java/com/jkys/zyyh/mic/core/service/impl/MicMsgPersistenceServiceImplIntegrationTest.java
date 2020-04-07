package com.jkys.zyyh.mic.core.service.impl;

import com.jkys.zyyh.mic.BaseSpringTest;
import com.jkys.zyyh.mic.core.service.MicMsgPersistenceService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author twj
 * 2020/1/21 15:29
 */
public class MicMsgPersistenceServiceImplIntegrationTest extends BaseSpringTest {
    @Autowired
    private MicMsgPersistenceServiceImpl micMsgPersistenceService;


    @Test
    @Disabled
    void testName() {
        micMsgPersistenceService.archiveMsg();
    }
}
