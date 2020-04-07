package com.jkys.zyyh.mic.api.service;

import com.jkys.hhc.conn.model.DoctorOrderModel;
import com.jkys.zyyh.mic.api.common.HisMessageContext;
import com.jkys.zyyh.penida.core.spi.Combinator;
import com.jkys.zyyh.penida.spring.SpringServiceOrchestrator;

import java.util.List;
import java.util.function.Function;

/**
 * @ClassName DoctorOrderListParser
 * @Description
 * @Author longchen
 * @Date 2020/4/7 8:44
 * @Version V1.0
 */
public interface DoctorOrderListParser extends Function<HisMessageContext, List<DoctorOrderModel>>,
        SpringServiceOrchestrator<DoctorOrderListParser> {

    @Override
    default Combinator tree() {
        //插件系统使用chain(dynamicProviders()); 可以加载二方包的类
        return chain(dynamicProviders());
    }
}
