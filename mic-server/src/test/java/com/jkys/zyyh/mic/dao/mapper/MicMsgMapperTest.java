package com.jkys.zyyh.mic.dao.mapper;

import com.jkys.agilegson.AGSON;
import com.jkys.zyyh.mic.BaseSpringTest;
import com.jkys.zyyh.mic.common.common.MicMessageBizType;
import com.jkys.zyyh.mic.dao.entity.MicMsg;
import com.jkys.zyyh.mic.dao.query.MicMsgQuery;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author twj
 * 2020/1/2 16:29
 */
class MicMsgMapperTest extends BaseSpringTest {

    @Autowired
    MicMsgMapper micMsgMapper;

    @Test
    @Disabled
    void testQueryList() {
        MicMsgQuery query = new MicMsgQuery();
        System.out.println(AGSON.toJSON(micMsgMapper.queryList(query)));
        query.setMicBizType(MicMessageBizType.DEPT);
        System.out.println(AGSON.toJSON(micMsgMapper.queryList(query)));
        query = new MicMsgQuery();
        query.setMicBizEvent("A");
        System.out.println(AGSON.toJSON(micMsgMapper.queryList(query)));
        query = new MicMsgQuery();
        query.setWithContent(true);
        System.out.println(AGSON.toJSON(micMsgMapper.queryList(query)));
        query = new MicMsgQuery();
        query.setConsume(1);
        System.out.println(AGSON.toJSON(micMsgMapper.queryList(query)));
    }

    @Test
    @Disabled
    void testSelectOne() {
        MicMsgQuery query = new MicMsgQuery();
        System.out.println(AGSON.toJSONString(micMsgMapper.selectOne(query)));

        query = new MicMsgQuery();
        query.setId(1L);
        System.out.println(AGSON.toJSONString(micMsgMapper.selectOne(query)));

        query = new MicMsgQuery();
        query.setMsgIdentify("A");
        System.out.println(AGSON.toJSONString(micMsgMapper.selectOne(query)));
    }

    @Test
    @Transactional
    @Disabled
    void testInsert() {
        MicMsg micMsg = new MicMsg();
        micMsg.setMsgIdentify("a");
        micMsg.setMicBizType(MicMessageBizType.PATIENT);
        micMsg.setMicBizEvent("A");
        micMsg.setConsume(1);
        micMsg.setRetryCount(1);
        micMsg.setMaxRetryCount(1);

        micMsgMapper.insertSelective(micMsg);
    }


    @Test
    @Transactional
    @Disabled
    void testUpdateRetryCount() {
        micMsgMapper.updateRetryCount(1L);
    }
}