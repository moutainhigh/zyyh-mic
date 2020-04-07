package com.jkys.zyyh.mic.api.service;

import com.jkys.hhc.conn.model.DepartmentModel;
import com.jkys.zyyh.mic.BaseSpringTest;
import com.jkys.zyyh.mic.api.dto.SimpleHisMessageContext;
import com.jkys.zyyh.penida.core.ServiceOrchestrator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author twj
 * 2020/1/16 17:15
 */
class DeptMsgParserTest extends BaseSpringTest {

    private DeptMsgParser deptMsgParser = ServiceOrchestrator.getOrchestrator(DeptMsgParser.class);

    @Test
    void test() {
        DepartmentModel r = deptMsgParser.apply(new SimpleHisMessageContext());
        Assertions.assertNotNull(r);
    }
}