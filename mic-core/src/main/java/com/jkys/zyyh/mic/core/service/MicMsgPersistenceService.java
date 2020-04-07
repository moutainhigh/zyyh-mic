package com.jkys.zyyh.mic.core.service;

import com.jkys.zyyh.mic.api.common.HisBizMessage;
import com.jkys.zyyh.mic.dao.entity.MicMsg;
import com.jkys.zyyh.mic.dao.query.MicMsgQuery;

import java.util.List;

/**
 * 消息持久化service
 *
 * @author twj
 * 2020/1/2 19:40
 */
public interface MicMsgPersistenceService {

    /**
     * 查询消息列表
     *
     * @param query
     * @return
     */
    List<MicMsg> query(MicMsgQuery query);

    /**
     * 查询某条消息
     *
     * @param query
     * @return
     */
    MicMsg selectOne(MicMsgQuery query);

    /**
     * 保存记录
     * 1. 无插入
     * 2. 有 不做操作
     * 通过计算 content的 hash值判断是否同一条消息
     *
     * @param hisBizMessage
     */
    void saveMsg(HisBizMessage hisBizMessage);

    /**
     * 根据消息ID更新消息的重试次数
     *
     * @param id 消息ID
     */
    void updateRetryCount(Long id);
}
