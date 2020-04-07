package com.jkys.zyyh.mic.dao.entity;

import com.jkys.agilegson.AGSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import java.lang.reflect.InvocationTargetException;

/**
 * @author twj
 * 2020/1/7 11:36
 */
class MicMsgHistoryTest {

    @Test
    void testCopyFromMicMsg() {
        MicMsg micMsg = new MicMsg();
        micMsg.setId(1L);

        MicMsgHistory micMsgHistory = new MicMsgHistory();
        try {
            BeanUtils.copyProperties(micMsg, micMsgHistory);
            System.out.println(AGSON.toJSONString(micMsgHistory));
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }
}