package com.jkys.zyyh.mic.feature.testhospital;

import com.jkys.agilegson.AGSON;
import com.jkys.hhc.conn.model.EmployeeModel;
import com.jkys.zyyh.mic.api.common.HisMessageContext;
import com.jkys.zyyh.mic.api.service.EmpMsgParser;
import com.jkys.zyyh.mic.common.common.HospitalIdConstant;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author twj
 * 2020/1/19 15:31
 */
@Service
public class TestEmpMsgParser implements EmpMsgParser {
    @Override
    public EmployeeModel apply(HisMessageContext hisMessageContext) {
        if (Objects.equals(HospitalIdConstant.TEST_ID, hisMessageContext.getHospitalId()) == false) {
            //继续处理
            return null;
        }

        //解析
        return AGSON.parseObject(hisMessageContext.getHisBizMessage().getContent(), EmployeeModel.class);
    }
}
