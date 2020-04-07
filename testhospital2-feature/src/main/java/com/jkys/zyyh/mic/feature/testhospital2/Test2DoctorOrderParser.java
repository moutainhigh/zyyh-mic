package com.jkys.zyyh.mic.feature.testhospital2;

import com.jkys.agilegson.AGSON;
import com.jkys.hhc.conn.model.DoctorOrderModel;
import com.jkys.zyyh.mic.api.common.HisMessageContext;
import com.jkys.zyyh.mic.api.service.DoctorOrderParser;
import com.jkys.zyyh.mic.common.common.HospitalIdConstant;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author twj
 * 2020/1/19 15:32
 */
@Service
public class Test2DoctorOrderParser implements DoctorOrderParser {
    @Override
    public DoctorOrderModel apply(HisMessageContext hisMessageContext) {
        if (Objects.equals(HospitalIdConstant.TEST2_ID, hisMessageContext.getHospitalId()) == false) {
            //继续处理
            return null;
        }

        //解析
        return AGSON.parseObject(hisMessageContext.getHisBizMessage().getContent(), DoctorOrderModel.class);
    }
}
