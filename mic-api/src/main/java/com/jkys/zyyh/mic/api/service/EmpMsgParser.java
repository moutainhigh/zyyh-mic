package com.jkys.zyyh.mic.api.service;

import com.jkys.hhc.conn.model.EmployeeModel;
import com.jkys.hhc.conn.model.WardModel;
import com.jkys.zyyh.mic.api.common.HisBizMessage;
import com.jkys.zyyh.mic.api.common.HisMessageContext;
import com.jkys.zyyh.penida.core.spi.Combinator;
import com.jkys.zyyh.penida.spring.SpringServiceOrchestrator;

import java.util.function.Function;

/**
 * his医护消息解析
 * @author twj
 * @version 1.0.0
 * 2020/1/7 20:14
 */
public interface EmpMsgParser extends Function<HisMessageContext, EmployeeModel>,
        SpringServiceOrchestrator<EmpMsgParser> {

    @Override
    default Combinator tree() {
        //插件系统使用chain(dynamicProviders()); 可以加载二方包的类
        return chain(dynamicProviders());
    }
}
