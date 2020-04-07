package com.jkys.zyyh.mic.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.jkys.agilegson.AGSON;
import com.jkys.zyyh.mic.common.enums.BooleanFlagEnum;
import com.jkys.zyyh.mic.common.util.IteratorPageUtils;
import com.jkys.zyyh.mic.core.service.MicMsgPersistenceService;
import com.jkys.zyyh.mic.api.common.HisBizMessage;
import com.jkys.zyyh.mic.dao.entity.MicMsg;
import com.jkys.zyyh.mic.dao.entity.MicMsgHistory;
import com.jkys.zyyh.mic.dao.mapper.MicMsgHistoryMapper;
import com.jkys.zyyh.mic.dao.mapper.MicMsgMapper;
import com.jkys.zyyh.mic.dao.query.MicMsgQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author twj
 * 2020/1/3 10:25
 */
@Service
@Slf4j
@Primary
public class MicMsgPersistenceServiceImpl implements MicMsgPersistenceService {
    private static final int DEFAULT_MAX_RETRY_COUNT = 10;
    private static final int DEFAULT_RETRY_COUNT = 0;
    private static final int DEFAULT_PAGE_SIZE = 500;

    @Autowired
    private MicMsgMapper micMsgMapper;
    @Autowired
    private MicMsgHistoryMapper micMsgHistoryMapper;

    @Override
    public List<MicMsg> query(MicMsgQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        return micMsgMapper.queryList(query);
    }

    @Override
    public MicMsg selectOne(MicMsgQuery query) {
        return micMsgMapper.selectOne(query);
    }

    @Override
    public void saveMsg(HisBizMessage hisBizMessage) {
        //TODO 保存消息
        //sha256 hash算法
        String msgIdent = computeMsgIdent(hisBizMessage);

        MicMsgQuery query = new MicMsgQuery();
        query.setMsgIdentify(msgIdent);

        MicMsg exist = getSpecificMsg(query);

        //不存在
        if (Objects.isNull(exist)) {
            MicMsg micMsg = new MicMsg();
            micMsg.setMsgIdentify(msgIdent);
            micMsg.setConsume(BooleanFlagEnum.NO.getValue());
            micMsg.setRetryCount(DEFAULT_RETRY_COUNT);
            micMsg.setMaxRetryCount(DEFAULT_MAX_RETRY_COUNT);
            micMsg.setContent(hisBizMessage.getContent());
            micMsg.setMicBizType(hisBizMessage.getMicBizType());
            micMsg.setMicBizEvent(hisBizMessage.getMicBizEvent());

            micMsgMapper.insertSelective(micMsg);

            log.info("msg saved msg identify: [{}]", micMsg.getMsgIdentify());
        } else {
            logExistMsg(exist);
        }
    }

    /**
     * 计算 his消息的唯一值
     * 可以不同医院调整
     * 计算方法
     * sha256(content + type + event)
     *
     * @param hisBizMessage
     */
    String computeMsgIdent(HisBizMessage hisBizMessage) {
        //先用 content_type_event 计算
        String identString = hisBizMessage.getContent() + "_" + hisBizMessage.getMicBizType() + "_" + hisBizMessage.getMicBizEvent();
        String msgIdent = DigestUtils.sha256Hex(identString);
        return msgIdent;
    }

    /**
     * mic_msg 查询
     * mic_msg_history 查询
     *
     * @param query
     * @return
     */
    MicMsg getSpecificMsg(MicMsgQuery query) {
        MicMsg micmsg = micMsgMapper.selectOne(query);

        if (micmsg != null) {
            return micmsg;
        } else {
            //再次查询归档
            return micMsgHistoryMapper.selectOne(query);
        }
    }

    /**
     * 现有消息记录日志
     *
     * @param exist
     */
    void logExistMsg(MicMsg exist) {
        try {
            MicMsg copyMicMsg = new MicMsg();
            BeanUtils.copyProperties(exist, copyMicMsg);

            copyMicMsg.setContent(null);

            if (Objects.equals(exist.getConsume(), BooleanFlagEnum.YES.getValue())) {
                log.warn("该消息，院内已处理 msg: [{}]", AGSON.toJSONString(copyMicMsg));
            } else if (exist instanceof MicMsgHistory) {
                log.warn("该消息，已归档 msg: [{}]", AGSON.toJSONString(copyMicMsg));
            } else if (exist.getRetryCount() > DEFAULT_RETRY_COUNT && exist.getRetryCount() < exist.getMaxRetryCount()) {
                log.warn("该消息，院内正在处理 msg: [{}]", AGSON.toJSONString(copyMicMsg));
            } else if (exist.getRetryCount() >= exist.getMaxRetryCount()) {
                log.warn("该消息，未处理成功 msg: [{}]", AGSON.toJSONString(copyMicMsg));
            } else {
                log.warn("该消息，已保存 待处理 msg: [{}]", AGSON.toJSONString(copyMicMsg));
            }
        } catch (BeansException e) {
            log.error("", e);
        }
    }

    /**
     * 更新消息重试次数
     *
     * @param id 消息ID
     */
    @Override
    public void updateRetryCount(Long id) {
        //TODO 修改成代码 +1
        micMsgMapper.updateRetryCount(id);
    }

    /**
     * 定时归档消息
     * 1. 归档已处理消息
     * 2. 一天前的消息
     * 3. 500批量归档
     * 事务
     */
    @Transactional
    public void archiveMsg() {

        MicMsgQuery query = new MicMsgQuery();
        query.setWithContent(true);
        query.setConsume(BooleanFlagEnum.YES.getValue());
        //一天前的数据
        query.setGmtCreateEnd(DateUtils.addDays(new Date(), -1));

        //分页迭代
        IteratorPageUtils.iterateElements((pageIndex, pageSize) -> {
            PageHelper.startPage(pageIndex, pageSize);
            List<MicMsg> micMsgs = micMsgMapper.queryList(query);
            log.info("archive list size: [{}]", CollectionUtils.size(micMsgs));

            List<Long> micMsgIds = ListUtils.emptyIfNull(micMsgs).stream().map(MicMsg::getId).sorted().collect(Collectors.toList());

            log.info("archive id list : [{}]", micMsgIds);
            return micMsgs;
        }, archiveMsgList -> {
            //归档数据
            List<MicMsgHistory> insertList = new ArrayList<>(archiveMsgList.size());
            Set<Long> idSet = new HashSet<>(archiveMsgList.size());

            for (MicMsg micMsg : archiveMsgList) {
                try {
                    MicMsgHistory micMsgHistory = new MicMsgHistory();
                    BeanUtils.copyProperties(micMsg, micMsgHistory);
                    micMsgHistory.setArchiveTime(new Date());
                    insertList.add(micMsgHistory);
                    idSet.add(micMsg.getId());
                } catch (BeansException e) {
                    log.error("bean copy 失败", e);
                }
            }

            //批量保存
            micMsgHistoryMapper.insertBatch(insertList);

            //删除
            micMsgMapper.deleteByIds(idSet);

        }, DEFAULT_PAGE_SIZE);

        log.info("mic_msg archive accomplish");
    }
}
