package com.jkys.zyyh.mic.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author twj
 * 2020/1/2 19:14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MicMsgHistory extends MicMsg {
    //归档时间
    private Date archiveTime;

    public Date getArchiveTime() {
        return archiveTime;
    }

    public void setArchiveTime(Date archiveTime) {
        this.archiveTime = archiveTime;
    }

}
