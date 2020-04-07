package com.jkys.zyyh.mic.core.service.impl;

import com.jkys.agilegson.AGSON;
import com.jkys.zyyh.mic.core.dto.HospitalResponse;
import com.jkys.zyyh.mic.core.dto.HospitalMessage;
import com.jkys.zyyh.mic.core.dto.MsgSendSuccessFlag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * HTTP方式发送消息处理器.
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-14 14:40
 */
@Order(1)
@Service("HttpHospitalMsgSubmitter")
@PropertySource("classpath:mic.properties")
@Slf4j
public class HttpHospitalMsgSubmitter extends BaseHospitalMsgSubmitter {

    public static final String MIC_TOKEN = "9DE8703B9CDD49AFADC85C0DB2297025";
    public static final String URL = "http://127.0.0.1:9096/mic/acceptMsg";
    public static final int SUCCESS_CODE = 2000;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void doSubmit(MsgSendSuccessFlag msgSendSuccessFlag, HospitalMessage hospitalMessage) {
        log.info("http method send msg");

        String postbody = AGSON.toJSONString(hospitalMessage);

        log.debug("post json body: [{}]", postbody);

        HttpEntity<String> entity = new HttpEntity<>(postbody, getJsonHeader());
        ResponseEntity<HospitalResponse> response = restTemplate.postForEntity(URL, entity, HospitalResponse.class);
        HospitalResponse hospitalResponse = response.getBody();
        if (Objects.equals(response.getStatusCode(), HttpStatus.OK) && Objects.equals(hospitalResponse.getCode(), SUCCESS_CODE)) {
            msgSendSuccessFlag.setSendSuccess(true);
        } else {
            log.warn("hospital msg submit failed, response: [{}] post body: [{}]", AGSON.toJSONString(response), postbody);
        }
    }

    /**
     * 设置请求头.
     *
     * @return
     */
    private HttpHeaders getJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add("MicToken", MIC_TOKEN);
        return headers;
    }
}
