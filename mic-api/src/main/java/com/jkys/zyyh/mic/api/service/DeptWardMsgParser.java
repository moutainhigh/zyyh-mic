package com.jkys.zyyh.mic.api.service;

import com.jkys.hhc.conn.model.DepartmentWardModel;
import com.jkys.zyyh.mic.api.common.HisMessageContext;
import com.jkys.zyyh.penida.core.spi.Combinator;
import com.jkys.zyyh.penida.spring.SpringServiceOrchestrator;

import java.util.function.Function;

/**
 * 科室 病区 科室病区 解析器
 *
 * @author twj
 * @version 1.0.0
 * 2020/1/7 20:08
 */
public interface DeptWardMsgParser extends Function<HisMessageContext, DepartmentWardModel>,
        SpringServiceOrchestrator<DeptWardMsgParser> {

    @Override
    default Combinator tree() {
        //插件系统使用chain(dynamicProviders()); 可以加载二方包的类
        return chain(dynamicProviders());
    }
}
