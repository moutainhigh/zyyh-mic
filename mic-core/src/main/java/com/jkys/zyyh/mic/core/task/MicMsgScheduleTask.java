package com.jkys.zyyh.mic.core.task;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.jkys.zyyh.mic.common.util.IteratorPageUtils;
import com.jkys.zyyh.mic.core.dto.MicHisBizMessage;
import com.jkys.zyyh.mic.core.service.MicMsgPersistenceService;
import com.jkys.zyyh.mic.core.service.MsgProcessor;
import com.jkys.zyyh.mic.core.service.impl.MicMsgPersistenceServiceImpl;
import com.jkys.zyyh.mic.dao.entity.MicMsg;
import com.jkys.zyyh.mic.dao.mapper.MicMsgMapper;
import com.jkys.zyyh.mic.dao.query.MicMsgQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 定时查询消息任务类
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-02 15:20
 */
@Slf4j
@Configuration
@EnableScheduling
public class MicMsgScheduleTask {

    private static final int PAGE_SIZE = 500;

    @Autowired
    private MicMsgPersistenceService micMsgPersistenceService;
    @Autowired
    private List<MsgProcessor> msgProcessorList;
    @Autowired
    private MicMsgMapper micMsgMapper;

    /**
     * 轮询发送未成功消费的消息
     */
    @Scheduled(fixedDelayString = "${mic.msg.schedule-fixed-delay:30000}")
    private void handleUnConsumeMsgTask() {
        //性能日志
        log.info("Start processing unsuccessfully consumed messages," +
                "time is [{}]", System.currentTimeMillis());
        // 未处理的消息
        int unConsumeSum = micMsgMapper.unConsumeSum();
        // 页数
        int num = micMsgMapper.unConsumeSum() / PAGE_SIZE + 1;
        log.info("There are [{}] unsuccessful messages", unConsumeSum);
        for (int i = 1; i <= num; i++) {
            MicMsgQuery micMsgQuery = new MicMsgQuery();
            micMsgQuery.setConsume(0);
            micMsgQuery.setWithContent(true);
            micMsgQuery.setPageParam(i, PAGE_SIZE);
            List<MicMsg> micMsgList = micMsgPersistenceService.query(micMsgQuery);
            List<MicMsg> micMsgs = micMsgList
                    .stream()
                    // 重试次数小于最大重试次数
                    .filter(micMsg -> micMsg.getRetryCount() < micMsg.getMaxRetryCount())
                    .collect(Collectors.toList());
            log.info("This time resend [{}]", micMsgs.size());
            for (MicMsg micMsg : micMsgs) {

                MicHisBizMessage micHisBizMessage = new MicHisBizMessage(micMsg);

                // 提交给消息业务路由模块
                for (MsgProcessor msgProcessor : msgProcessorList) {
                    if (msgProcessor.process(micHisBizMessage)) {
                        break;
                    }
                }
            }
        }
        log.info("The time for successfully processing unconsumed messages is [{}]", System.currentTimeMillis());
    }

    /**
     * 每天 1:01 归档 mic_msg -> mic_msg_history
     */
    @Scheduled(cron = "0 1 1 * * ?")
    public void archiveMsg() {
        ((MicMsgPersistenceServiceImpl) micMsgPersistenceService).archiveMsg();
    }

}
