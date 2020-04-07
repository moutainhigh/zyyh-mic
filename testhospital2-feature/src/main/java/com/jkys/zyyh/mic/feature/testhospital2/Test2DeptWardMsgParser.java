package com.jkys.zyyh.mic.feature.testhospital2;

import com.jkys.agilegson.AGSON;
import com.jkys.hhc.conn.model.DepartmentWardModel;
import com.jkys.zyyh.mic.api.common.HisMessageContext;
import com.jkys.zyyh.mic.api.service.DeptWardMsgParser;
import com.jkys.zyyh.mic.common.common.HospitalIdConstant;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author twj
 * 2020/1/19 15:31
 */
@Service
public class Test2DeptWardMsgParser implements DeptWardMsgParser {
    @Override
    public DepartmentWardModel apply(HisMessageContext hisMessageContext) {

        if (Objects.equals(HospitalIdConstant.TEST2_ID, hisMessageContext.getHospitalId()) == false) {
            //继续处理
            return null;
        }

        //解析
        return AGSON.parseObject(hisMessageContext.getHisBizMessage().getContent(), DepartmentWardModel.class);
    }
}
