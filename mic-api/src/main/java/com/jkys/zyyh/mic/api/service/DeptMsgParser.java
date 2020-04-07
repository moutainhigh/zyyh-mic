package com.jkys.zyyh.mic.api.service;

import com.jkys.hhc.conn.model.DepartmentModel;
import com.jkys.zyyh.mic.api.common.HisMessageContext;
import com.jkys.zyyh.penida.core.spi.Combinator;
import com.jkys.zyyh.penida.spring.SpringServiceOrchestrator;

import java.util.function.Function;

/**
 * 科室消息 解析
 * @author twj
 * 2020/1/16 16:43
 */
public interface DeptMsgParser extends Function<HisMessageContext, DepartmentModel>,
        SpringServiceOrchestrator<DeptMsgParser> {

    @Override
    default Combinator tree() {
        //插件系统使用chain(dynamicProviders()); 可以加载二方包的类
        return chain(dynamicProviders());
    }
}
