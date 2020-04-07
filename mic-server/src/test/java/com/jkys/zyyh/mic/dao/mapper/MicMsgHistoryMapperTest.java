package com.jkys.zyyh.mic.dao.mapper;

import com.google.common.collect.ImmutableList;
import com.jkys.agilegson.AGSON;
import com.jkys.zyyh.mic.BaseSpringTest;
import com.jkys.zyyh.mic.common.common.MicMessageBizType;
import com.jkys.zyyh.mic.dao.entity.MicMsgHistory;
import com.jkys.zyyh.mic.dao.query.MicMsgQuery;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author twj
 * 2020/1/2 20:01
 */
public class MicMsgHistoryMapperTest extends BaseSpringTest {
    @Autowired
    MicMsgHistoryMapper micMsgHistoryMapper;

    @Test
    @Disabled
    @Transactional
    public void testinsertBatch() {
        MicMsgHistory micMsgHistory = new MicMsgHistory();
        micMsgHistory.setId(1L);
        micMsgHistory.setMsgIdentify("a");
        micMsgHistory.setMicBizType(MicMessageBizType.DEPT);
        micMsgHistory.setMicBizEvent("A");
        micMsgHistory.setConsume(1);
        micMsgHistory.setRetryCount(1);
        micMsgHistory.setMaxRetryCount(1);

        micMsgHistoryMapper.insertBatch(ImmutableList.of(micMsgHistory));

        MicMsgHistory micMsgHistory_1 = new MicMsgHistory();
        micMsgHistory_1.setId(2L);
        micMsgHistory_1.setMsgIdentify("b");
        micMsgHistory_1.setMicBizType(MicMessageBizType.DEPT);
        micMsgHistory_1.setMicBizEvent("A");
        micMsgHistory_1.setConsume(1);
        micMsgHistory_1.setRetryCount(1);
        micMsgHistory_1.setMaxRetryCount(1);

        MicMsgHistory micMsgHistory_2 = new MicMsgHistory();
        micMsgHistory_2.setId(3L);
        micMsgHistory_2.setMsgIdentify("c");
        micMsgHistory_2.setMicBizType(MicMessageBizType.DEPT);
        micMsgHistory_2.setMicBizEvent("A");
        micMsgHistory_2.setConsume(1);
        micMsgHistory_2.setRetryCount(1);
        micMsgHistory_2.setMaxRetryCount(1);
        micMsgHistoryMapper.insertBatch(ImmutableList.of(micMsgHistory_1, micMsgHistory_2));
    }

    @Test
    @Transactional
    void testselectOne() {
        MicMsgQuery query = new MicMsgQuery();
        System.out.println(AGSON.toJSONString(micMsgHistoryMapper.selectOne(query)));

        query = new MicMsgQuery();
        query.setId(1L);
        System.out.println(AGSON.toJSONString(micMsgHistoryMapper.selectOne(query)));

        query = new MicMsgQuery();
        query.setMsgIdentify("A");
        query.setWithContent(true);
        System.out.println(AGSON.toJSONString(micMsgHistoryMapper.selectOne(query)));
    }
}
