package com.jkys.zyyh.mic.api.service.mock;

import com.jkys.hhc.conn.model.DepartmentModel;
import com.jkys.zyyh.mic.api.common.HisMessageContext;
import com.jkys.zyyh.mic.api.service.DeptMsgParser;
import org.springframework.stereotype.Service;

/**
 * @author twj
 * 2020/1/16 17:16
 */
@Service
public class DeptMsgParserMock implements DeptMsgParser {
    @Override
    public DepartmentModel apply(HisMessageContext hisMessageContext) {
        return new DepartmentModel();
    }
}
