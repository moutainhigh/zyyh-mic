package com.jkys.zyyh.mic.core.service.impl;

import com.google.common.collect.ImmutableList;
import com.jkys.zyyh.mic.api.common.HisBizMessage;
import com.jkys.zyyh.mic.api.dto.SimpleHisMessage;
import com.jkys.zyyh.mic.common.common.MicMessageBizType;
import com.jkys.zyyh.mic.common.enums.BooleanFlagEnum;
import com.jkys.zyyh.mic.dao.entity.MicMsg;
import com.jkys.zyyh.mic.dao.entity.MicMsgHistory;
import com.jkys.zyyh.mic.dao.mapper.MicMsgHistoryMapper;
import com.jkys.zyyh.mic.dao.mapper.MicMsgMapper;
import com.jkys.zyyh.mic.dao.query.MicMsgQuery;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author twj
 * 2020/1/7 16:38
 */
@ExtendWith(MockitoExtension.class)
class MicMsgPersistenceServiceImplTest {

    @InjectMocks
    @Spy
    MicMsgPersistenceServiceImpl micMsgService;
    @Mock
    private MicMsgMapper micMsgMapper;
    @Mock
    private MicMsgHistoryMapper micMsgHistoryMapper;

    @Test
    void testSaveMsg() {
        MicMsg micMsg = new MicMsg();
        micMsg.setId(1L);

        //case1
        doReturn(null).when(micMsgService).getSpecificMsg(any());

        micMsgService.saveMsg(new HisBizMessage() {
            @Override
            public String getMicBizType() {
                return null;
            }

            @Override
            public String getMicBizEvent() {
                return null;
            }

            @Override
            public String getContent() {
                return null;
            }
        });

        Mockito.verify(micMsgMapper).insertSelective(any());

        //case2
        Mockito.reset(micMsgService);
        Mockito.reset(micMsgMapper);

        Mockito.doNothing().when(micMsgService).logExistMsg(any());
        doReturn(micMsg).when(micMsgService).getSpecificMsg(any());

        micMsgService.saveMsg(new HisBizMessage() {
            @Override
            public String getMicBizType() {
                return null;
            }

            @Override
            public String getMicBizEvent() {
                return null;
            }

            @Override
            public String getContent() {
                return null;
            }
        });

        Mockito.verify(micMsgMapper, times(0)).insertSelective(any());
        Mockito.verify(micMsgService, times(1)).logExistMsg(any());
    }

    @Test
    void testgetSpecificMsg() {
        //case1
        MicMsg micMsg = new MicMsg();

        when(micMsgMapper.selectOne(any())).thenReturn(micMsg);

        MicMsg result = micMsgService.getSpecificMsg(new MicMsgQuery());

        Assertions.assertTrue(micMsg == result);

        //case2
        reset(micMsgService);
        MicMsgHistory micMsgHistory = new MicMsgHistory();

        when(micMsgMapper.selectOne(any())).thenReturn(null);
        when(micMsgHistoryMapper.selectOne(any())).thenReturn(micMsgHistory);

        result = micMsgService.getSpecificMsg(new MicMsgQuery());
        Assertions.assertTrue(micMsgHistory == result);

    }

    @Test
    void testComputeMsgIdent() {
        SimpleHisMessage hisBizMessage = new SimpleHisMessage();
        hisBizMessage.setContent("a");
        hisBizMessage.setMicBizType(MicMessageBizType.DEPT);
        hisBizMessage.setMicBizEvent("1");

        String identString = hisBizMessage.getContent() + "_" + hisBizMessage.getMicBizType() + "_" + hisBizMessage.getMicBizEvent();
        String msgIdent = DigestUtils.sha256Hex(identString);

        String result = micMsgService.computeMsgIdent(hisBizMessage);
        Assertions.assertEquals(msgIdent, result);
    }

    @Test
    void testLogExistMsg() {
        MicMsg micMsg = new MicMsg();

        micMsg.setConsume(BooleanFlagEnum.YES.getValue());

        micMsgService.logExistMsg(micMsg);

        micMsg = new MicMsg();
        micMsg.setRetryCount(1);
        micMsg.setMaxRetryCount(10);
        micMsgService.logExistMsg(micMsg);

        micMsg = new MicMsg();
        micMsg.setRetryCount(0);
        micMsg.setMaxRetryCount(10);
        micMsgService.logExistMsg(micMsg);
    }

    @Test
    void testUpdateRetryCount() {

    }

    @Test
    void testArchiveMsg() {
        MicMsg micMsg_0 = new MicMsg();
        micMsg_0.setId(1L);
        micMsg_0.setMicBizType(MicMessageBizType.DEPT);
        MicMsg micMsg_1 = new MicMsg();
        micMsg_1.setId(2L);
        micMsg_1.setMicBizType(MicMessageBizType.DEPT);

        List<MicMsg> archiveMsgList = ImmutableList.of(micMsg_0, micMsg_1);

        when(micMsgMapper.queryList(any())).thenReturn(archiveMsgList);

        micMsgService.archiveMsg();

        ArgumentCaptor<List> acInsert = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Set> acIdSet = ArgumentCaptor.forClass(Set.class);

        verify(micMsgHistoryMapper).insertBatch(acInsert.capture());
        verify(micMsgMapper).deleteByIds(acIdSet.capture());

        List<MicMsgHistory> insertMsgHistory = (List<MicMsgHistory>) acInsert.getValue();
        Set<Long> deleteIdSet = (Set<Long>) acIdSet.getValue();

        Assertions.assertTrue(insertMsgHistory.size() == 2);
        Assertions.assertEquals(1L, insertMsgHistory.get(0).getId());
        Assertions.assertEquals(2L, insertMsgHistory.get(1).getId());
        Assertions.assertTrue(deleteIdSet.size() == 2);
        Assertions.assertTrue(deleteIdSet.contains(1L));
        Assertions.assertTrue(deleteIdSet.contains(2L));

    }
}