package com.jkys.zyyh.mic.server.common;

import com.jkys.agilegson.AGSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * <Description> <br>
 * 日志拦截器
 * @author Rocky<br>
 * @version 1.0<br>
 * @createDate 2019/11/01 10:48 上午 <br>
 */
@Component
@Aspect
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    /**
     * 外部接口调用的日志监控
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* com.jkys.zyyh.mic.server.api..*.* (..))")
    public Object doRequestAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String name = method.getName();
        Object[] args = joinPoint.getArgs();
        String traceId = String.valueOf(UUID.randomUUID());
        MDC.put("traceId",traceId);
        log.info("method {}, param {}", name, args);
        result = joinPoint.proceed();
        log.info("method {}, result {}", name, genResultString(result));
        MDC.clear();
        return result;
    }


    /**
     * 如果返回值字段超过了1024，则截取
     * @param result
     * @return
     */
    private String genResultString(Object result){
        //如果结果为空，只直接返回
        if(result == null){
            return null;
        }
        String val = AGSON.toJSONString(result);
        if(val.length() > 1024){
            return val.substring(0,1023);
        }
        return val;
    }
}
