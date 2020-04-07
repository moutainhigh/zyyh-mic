package com.jkys.zyyh.mic.feature.testhospital;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.jkys.agilegson.AGSON;
import com.jkys.zyyh.mic.api.dto.SimpleHisMessage;
import com.jkys.zyyh.mic.api.service.MicHisMsgService;
import com.jkys.zyyh.mic.common.common.MicMessageBizType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 测试医院 controller
 *
 * @author twj
 * 2020/1/19 15:28
 */
@RestController
@Slf4j
public class TestHospitalController {

    @Autowired
    private MicHisMsgService micHisMsgService;

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
    @RequestMapping("/testhospital")
    public String msg(@RequestBody TestHospitalRequest testHospitalRequest) {
        SimpleHisMessage simpleHisMessage = new SimpleHisMessage();
        if (TYPE_SET.contains(testHospitalRequest.getBizType()) == false){
            simpleHisMessage.setMicBizType(MicMessageBizType.UNKNOWN);
        } else {
            simpleHisMessage.setMicBizType(testHospitalRequest.getBizType());
        }

        //校验json
        String msgString = testHospitalRequest.getMsg();

        try {
            JsonObject jo = AGSON.parseObject(msgString);
        } catch (JsonSyntaxException e) {
            log.warn("post json syntax is wrong string: [{}]", msgString);
            return "json格式错误!";
        }

        simpleHisMessage.setMicBizEvent(testHospitalRequest.getBizEvent());
        simpleHisMessage.setContent(msgString);

        //接受消息
        micHisMsgService.receiveMsg(simpleHisMessage);

        return "ok";
    }
}
