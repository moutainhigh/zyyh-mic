package com.jkys.zyyh.mic.feature.testhospital;

import com.jkys.hhc.conn.model.PatientModel;
import com.jkys.zyyh.mic.api.common.HisMessageContext;
import com.jkys.zyyh.mic.api.service.PatientMsgParser;
import org.springframework.stereotype.Service;

/**
 * @ClassName TestTowPatientMsgParser
 * @Description
 * @Author Gabriel
 * @Date 2020/2/26 21:51
 * @Version V1.0
 */
@Service
public class TestTowPatientMsgParser implements PatientMsgParser {

    @Override
    public PatientModel apply(HisMessageContext hisMessageContext) {
        System.out.println("TestTowPatientMsgParser 执行");
        return null;
    }
}