package com.jkys.zyyh.mic.api.common;

/**
 * his 业务消息消息接口
 *
 * @author twj
 * 2020/1/2 13:42
 */
public interface HisBizMessage extends HisMessage {

    /**
     * 获取消息业务类型
     * 科室、病人、医嘱等
     *
     * @return
     */
    String getMicBizType();

    /**
     * 获取消息的业务事件
     * 比如
     * 病人
     * 入院，出院
     * 医嘱
     * 下达，撤销
     *
     * @return
     */
    String getMicBizEvent();
}
