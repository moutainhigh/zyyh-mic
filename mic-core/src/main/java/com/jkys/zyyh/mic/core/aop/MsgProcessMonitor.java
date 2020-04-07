package com.jkys.zyyh.mic.core.aop;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息处理 监控类
 *
 * @author twj
 * 2020/2/12 15:58
 */
@Aspect
@Component
public class MsgProcessMonitor {
    private static final Logger exceptionLog = org.slf4j.LoggerFactory.getLogger("exceptionLog");
    //全部消息
    public static final String MIC_MSG_PROCESS_TIMER = "MIC_MSG_PROCESS_TIMER";
    //消息提交
    public static final String MIC_MSG_SUBMITTER_TIMER = "MIC_MSG_SUBMITTER_TIMER";

    @Autowired
    private MetricRegistry metricRegistry;

    //消息处理方法
    @Pointcut("execution(public * com.jkys.zyyh.mic.core.service.MsgProcessor.process(..))")
    public void processPointCut() {
    }

    @Around("processPointCut()")
    public Object msgProcessMonitor(ProceedingJoinPoint pjp) throws Throwable {

        //全部消息
        Timer msgProcessTimer = metricRegistry.timer(MIC_MSG_PROCESS_TIMER);
        Timer.Context timeContext = msgProcessTimer.time();
        Timer specificProcessTimer = metricRegistry.timer(pjp.getTarget().getClass().getName());
        Timer.Context specificProcessTimerContext = specificProcessTimer.time();

        Boolean result = false;

        try {
            result = (Boolean) pjp.proceed();
            return result;
        } finally {
            //处理成功
            if (Boolean.TRUE.equals(result)) {
                timeContext.stop();
                specificProcessTimerContext.stop();
            }
        }
    }

    //消息提交
    @Pointcut("execution(public * com.jkys.zyyh.mic.core.service.impl.MsgSendServiceImpl.sendMsg(..))")
    public void msgSubmiterPointCut() {
    }

    @Around("msgSubmiterPointCut()")
    public Object msgSubmiterPointCutAround(ProceedingJoinPoint pjp) throws Throwable {

        Timer submitterTimer = metricRegistry.timer(MIC_MSG_SUBMITTER_TIMER);
        Timer.Context timeContext = submitterTimer.time();

        Object[] args = pjp.getArgs();
        String micBizType = (String) args[0];
        String micBizEvent = (String) args[1];

        Timer specificSubmitterTimer = metricRegistry.timer(MIC_MSG_SUBMITTER_TIMER + "-" + micBizType + "_" + micBizEvent);
        Timer.Context specificSubmitterTimerContext = specificSubmitterTimer.time();

        try {
            return pjp.proceed();
        } finally {
            timeContext.stop();
            specificSubmitterTimerContext.stop();
        }
    }
}
