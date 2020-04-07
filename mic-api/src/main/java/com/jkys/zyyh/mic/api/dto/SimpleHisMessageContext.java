package com.jkys.zyyh.mic.api.dto;

import com.jkys.zyyh.mic.api.common.HisBizMessage;
import com.jkys.zyyh.mic.api.common.HisMessageContext;

/**
 * 简单实现
 * @author twj
 * 2020/1/16 16:47
 */
public class SimpleHisMessageContext implements HisMessageContext {

    private Long hospitalId;
    private HisBizMessage hisBizMessage;

    @Override
    public HisBizMessage getHisBizMessage() {
        return hisBizMessage;
    }

    public void setHisBizMessage(HisBizMessage hisBizMessage) {
        this.hisBizMessage = hisBizMessage;
    }

    @Override
    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }
}
