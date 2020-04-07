package com.jkys.zyyh.mic.api.common;

/**
 * his消息 上下文
 * 用于解析时使用
 * @author twj
 * 2020/1/16 16:45
 */
public interface HisMessageContext {

    /**
     * 医院id
     * @return
     */
    Long getHospitalId();

    HisBizMessage getHisBizMessage();
}
