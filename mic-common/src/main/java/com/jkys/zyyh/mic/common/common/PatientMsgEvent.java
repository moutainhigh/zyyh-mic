package com.jkys.zyyh.mic.common.common;

/**
 * 病人消息事件
 * @author twj
 * 2020/1/8 11:56
 */
public class PatientMsgEvent {
    //住院
    public static final String ADMITTED = "ADMITTED";
    //出院
    public static final String DISCHARGE = "DISCHARGE";

    //转科
    public static final String TRANSFER_DEPT = "TRANSFER_DEPT";

    /**
     * 基本信息更新，不涉及院内业务处理
     * 姓名 性别 生日 床位号 医生 护士 病区
     *
     */

    public static final String UPDATE = "UPDATE";
}
