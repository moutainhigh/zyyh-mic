package com.jkys.zyyh.mic.dao.query;

import lombok.Data;

import java.util.Date;

/**
 * @author twj
 * 2020/1/2 19:15
 */
@Data
public class MicMsgQuery extends PageQuery {

    private Long id;
    private String msgIdentify;

    //消息业务类型
    private String micBizType;
    //消息的业务事件
    private String micBizEvent;

    //包含 content字段
    private Boolean withContent;

    private Integer consume;

    // gmtCreate < gmtCreateEnd
    private Date gmtCreateEnd;

    // id < idEnd
    private Long idEnd;
}
