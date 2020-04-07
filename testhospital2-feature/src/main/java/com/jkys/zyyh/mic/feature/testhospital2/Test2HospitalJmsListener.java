package com.jkys.zyyh.mic.feature.testhospital2;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.jkys.agilegson.AGSON;
import com.jkys.zyyh.mic.api.dto.SimpleHisMessage;
import com.jkys.zyyh.mic.api.service.MicHisMsgService;
import com.jkys.zyyh.mic.common.common.MicMessageBizType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Set;

/**
 * 测试医院3 jms监听器
 *
 * @author twj
 * 2020/1/19 15:28
 */
@Service
@Slf4j
public class Test2HospitalJmsListener {

    @Autowired
    private MicHisMsgService micHisMsgService;

    @Autowired
    private JmsTemplate jmsTemplate;

    //支持的 消息类型
    private static final Set<String> TYPE_SET = ImmutableSet.of(MicMessageBizType.DEPT, MicMessageBizType.WARD, MicMessageBizType.DEPT_WARD,
            MicMessageBizType.EMP, MicMessageBizType.PATIENT, MicMessageBizType.DOCTOR_ORDER);

    /**
     * {
     * "bizType" : "DEPT",
     * "bizEvent" : "ADD",
     * "msg" : "" 字符串数据
     * }
     *
     * @return
     */
    @Scheduled(fixedRate = 10000, initialDelay = 1000)
    public void fetchJmsMsg() {
//第一种方法
//        Message msg = jmsTemplate.receive("mytestqueue");
//        System.out.println(msg);

    }

    /**
     * 用注解获取消息
     * mic_queue 为队列名称
     *
     * @param data
     */
    @JmsListener(destination = "mic_queue")
    public void listenMsg(Message data) {

        log.debug("receive jms msg: [{}]", data);

        TextMessage textmsg = (TextMessage) data;

        try {

            String jsonText = textmsg.getText();
            try {
                /*
                这里收到的jsonText是这样的
                {
                    "bizType": "PATIENT",
                        "bizEvent": "UPDATE",
                        "msg": "{\"inpatientNo\":\"HP60af9dc7-c8bb-4fae-b242-cb51c4c8912a\",\"outDay\":null ,\"outDeptDay\":null ,\"birthday\":-125136000,\"inDeptTime\":\"1581579016439\",\"hisPatientId\":\"HPaa4844a7-77cb-4b08-a315-09c4953b6a6a\",\"gender\":1,\"admissionTimes\":3,\"inDay\":\"1581579016439\",\"nurseCode\":\"HE4000389\",\"name\":\"病人10816\",\"doctorCode\":\"HE2000498\",\"wardCode\":\"HE2000498\",\"bedNumber\":\"A100403441\",\"deptCode\":\"HD200051\"}"
                }
                 */
                Test2HospitalRequest testHospitalRequest = AGSON.parseObject(jsonText, Test2HospitalRequest.class);

                SimpleHisMessage simpleHisMessage = new SimpleHisMessage();

                if (TYPE_SET.contains(testHospitalRequest.getBizType()) == false) {
                    simpleHisMessage.setMicBizType(MicMessageBizType.UNKNOWN);
                } else {
                    simpleHisMessage.setMicBizType(testHospitalRequest.getBizType());
                }

                //校验json
                String msgString = testHospitalRequest.getMsg();

                JsonObject jo = AGSON.parseObject(msgString);

                simpleHisMessage.setMicBizEvent(testHospitalRequest.getBizEvent());
                simpleHisMessage.setContent(msgString);

                //接受消息
                micHisMsgService.receiveMsg(simpleHisMessage);
            } catch (JsonSyntaxException e) {
                log.warn("post json syntax is wrong string: [{}]", jsonText);
            }

        } catch (JMSException e) {
            log.warn("deserialization jms msg error: [{}]", data);
        }

    }
}
