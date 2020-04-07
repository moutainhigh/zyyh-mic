package com.jkys.zyyh.mic.api.service;

import com.jkys.hhc.conn.model.PatientModel;
import com.jkys.hhc.conn.model.WardModel;
import com.jkys.zyyh.mic.api.common.HisBizMessage;
import com.jkys.zyyh.mic.api.common.HisMessageContext;
import com.jkys.zyyh.penida.core.spi.Combinator;
import com.jkys.zyyh.penida.spring.SpringServiceOrchestrator;

import java.util.function.Function;

/**
 * his病人消息解析器
 * @author twj
 * @version 1.0.0
 * 2020/1/7 20:19
 */
public interface PatientMsgParser extends Function<HisMessageContext, PatientModel>,
        SpringServiceOrchestrator<PatientMsgParser> {

    @Override
    default Combinator tree() {
        //插件系统使用chain(dynamicProviders()); 可以加载二方包的类
        return chain(dynamicProviders());
    }
}
