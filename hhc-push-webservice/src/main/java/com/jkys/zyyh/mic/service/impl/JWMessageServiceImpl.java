package com.jkys.zyyh.mic.service.impl;

import com.google.gson.JsonObject;
import com.jkys.agilegson.AGSON;
import com.jkys.zyyh.mic.api.dto.SimpleHisMessage;
import com.jkys.zyyh.mic.api.service.MicHisMsgService;
import com.jkys.zyyh.mic.common.common.DoctorOrderMsgEvent;
import com.jkys.zyyh.mic.common.common.MicMessageBizType;
import com.jkys.zyyh.mic.common.common.PatientMsgEvent;
import com.jkys.zyyh.mic.service.JWMessageService;
import com.jkys.zyyh.mic.utils.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName JWMessageServiceImpl
 * @Description 消息接收
 * @Author Gabriel
 * @Date 2020/4/3 10:07
 * @Version V1.0
 */
@Service
@WebService(serviceName = "JWMessageServiceImpl", endpointInterface = "com.jkys.zyyh.mic.service.JWMessageService")
@BindingType(javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
@Slf4j
public class JWMessageServiceImpl implements JWMessageService {

    @Autowired
    private MicHisMsgService micHisMsgService;

    @Autowired
    private RestTemplate restTemplate;

    private static Map<String, String> PATIENT_CODE = new HashMap<>();
    private static Map<String, String> DOCTOR_ORDER_CODE = new HashMap<>();
    private static String BLOOD_SUGAR_URL = "http://127.0.0.1:9096/hispdi/bloodsugar";
    private static String BLOOD_SUGAR_TOKEN = "9DE8703B9CDD49AFADC85C0DB2297025";
    private static String CONFIRM_BLOOD_SUGAR_URL = "http://127.0.0.1:9096/hispdi/bloodsugar/confirm";

    static {
        PATIENT_CODE.put("S0026", PatientMsgEvent.ADMITTED);
        PATIENT_CODE.put("S0027", PatientMsgEvent.UPDATE);
        PATIENT_CODE.put("S0029", PatientMsgEvent.TRANSFER_DEPT);
        PATIENT_CODE.put("S0030", PatientMsgEvent.UPDATE);
        PATIENT_CODE.put("S0032", PatientMsgEvent.DISCHARGE);
        PATIENT_CODE.put("S0033", PatientMsgEvent.UPDATE);
        DOCTOR_ORDER_CODE.put("S0035", DoctorOrderMsgEvent.ADD);
        DOCTOR_ORDER_CODE.put("S0036", DoctorOrderMsgEvent.UPDATE);
    }

    private static String SUCCESS = "<MCCI_IN000002UV01 xmlns=\"urn:hl7-org:v3\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
            "ITSVersion=\"XML_1.0\" xsi:schemaLocation=\"urn:hl7-org:v3 ../multicacheschemas/MCCI_IN000002UV01.xsd\">\n" +
            "\t<!-- 消息流水号 -->\n" +
            "\t<id root=\"2.16.156.10011.0\" extension=\"@%s\" />\n" +
            "\t<!-- 消息创建时间 -->\n" +
            "\t<creationTime value=\"%s\" />\n" +
            "\t<!-- 消息的服务标识-->\n" +
            "\t<interactionId root=\"2.16.840.1.113883.1.6\" extension=\"@MCCI_IN000002UV01\" />\n" +
            "\t<!--处理代码，标识此消息是否是产品、训练、调试系统的一部分。D：调试；P：产品；T：训练 -->\n" +
            "\t<processingCode code=\"P\" />\n" +
            "\t<!-- 消息处理模式: A(Archive); I(Initial load); R(Restore from archive); T(Current processing) -->\n" +
            "\t<processingModeCode/>\n" +
            "\t<!-- 消息应答: AL(Always); ER(Error/reject only); NE(Never) -->\n" +
            "\t<acceptAckCode code=\"AL\" />\n" +
            "\t<!-- 接受者 -->\n" +
            "\t<receiver typeCode=\"RCV\">\n" +
            "\t\t<device classCode=\"DEV\" determinerCode=\"INSTANCE\">\n" +
            "\t\t\t<!-- 接受者 ID -->\n" +
            "\t\t\t<id>\n" +
            "\t\t\t\t<item root=\"2.16.156.10011.0.1.1\" extension=\"@111\"/>\n" +
            "\t\t\t</id>\n" +
            "\t\t</device>\n" +
            "\t</receiver>\n" +
            "\t<!-- 发送者 -->\n" +
            "\t<sender typeCode=\"SND\">\n" +
            "\t\t<device classCode=\"DEV\" determinerCode=\"INSTANCE\">\n" +
            "\t\t\t<!-- 发送者 ID -->\n" +
            "\t\t\t<id>\n" +
            "\t\t\t\t<item root=\"2.16.156.10011.0.1.2\" extension=\"@222\"/>\n" +
            "\t\t\t</id>\n" +
            "\t\t</device>\n" +
            "\t</sender>\n" +
            "\t<!--typeCode 为处理结果，AA 表示成功 AE 表示失败-->\n" +
            "\t<acknowledgement typeCode=\"AA\">\n" +
            "\t\t<targetMessage>\n" +
            "\t\t\t<id extension=\"%s\"/>\n" +
            "\t\t</targetMessage>\n" +
            "\t\t<acknowledgementDetail>\n" +
            "\t\t\t<text value=\"%s\"/>\n" +
            "\t\t</acknowledgementDetail>\n" +
            "\t</acknowledgement>\n" +
            "</MCCI_IN000002UV01>";

    // 接收并处理消息
    @Override
    public String HIPMessageServer(String action, String message) {

        // 因为 带有命名空间不能进行 xml 解析，所以进行去除
        String msg = message.replaceAll("xmlns=\"urn:hl7-org:v3\"", "");

        if (StringUtils.isNotBlank(action)) {
            log.info("接收到新的同步消息：" + action + msg);

            SimpleHisMessage simpleHisMessage = new SimpleHisMessage();
            simpleHisMessage.setContent(msg);
            if (PATIENT_CODE.containsKey(action)) {
                simpleHisMessage.setMicBizType(MicMessageBizType.PATIENT);
                simpleHisMessage.setMicBizEvent(PATIENT_CODE.get(action));
            } else if (DOCTOR_ORDER_CODE.containsKey(action)) {
                simpleHisMessage.setMicBizType(MicMessageBizType.DOCTOR_ORDER);
                simpleHisMessage.setMicBizEvent(DOCTOR_ORDER_CODE.get(action));
            }

            micHisMsgService.receiveMsg(simpleHisMessage);
        }

        return String.format(
                SUCCESS, action,
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()),
                XmlUtil.getValueByXpath(XmlUtil.getDocument(msg), "//id/@extension"),
                "处理成功"
        );
    }

    @Override
    public String bloodSugarCallBack(String active, String message) {
        StringBuffer buffer = new StringBuffer();
        try {

            // 调用 pdi 血糖回写的接口获取血糖回写数据
            ResponseMessage response = getBloodSugar();

            log.info("获取血糖回写数据：[{}]", response);

            // 遍历血糖数据模型数据，可以通过 bloodSugarModel.get("id").getAsString() 获取数据，并进行处理
            // 这里使用 字符串拼接 + 字符串填充的方式组成 回写信息
            for (JsonObject bloodSugarModel : response.getData()) {
                System.out.println(bloodSugarModel.get("id"));
                System.out.println(bloodSugarModel);
                buffer.append("返回数据");
                buffer.append(bloodSugarModel.get("id").getAsString());

                // 测试 base64 加密工具
                System.out.println(XmlUtil.getBase64(SUCCESS).replace("\r\n",""));
                System.out.println(XmlUtil.getBase64(SUCCESS));
            }

            // 调用 pdi 回写确认接口，改变已经回写的血糖数据的状态
            if (!response.getData().isEmpty()) {
                log.info("血糖回写结果确认 {}",response.getData().stream().map(jsonObject -> jsonObject.get("id").getAsString()).collect(Collectors.toList()) );
                confirmBloodSugar(response);
            }
        } catch (RestClientException e) {
            log.warn("血糖回写失败", e);
        }

        return buffer.toString();
    }

    private ResponseMessage getBloodSugar() {
        HttpEntity<String> entity = buildHttpEntity(null);
        ResponseEntity<String> responseEntity = restTemplate.exchange(BLOOD_SUGAR_URL, HttpMethod.GET, entity, String.class);
        String body = responseEntity.getBody();
        return AGSON.parseObject(body, ResponseMessage.class);
    }

    private void confirmBloodSugar(ResponseMessage responseMessage) {
        try {
            HttpEntity<String> entity = buildHttpEntity(AGSON.toJSONString(responseMessage));
            restTemplate.postForEntity(CONFIRM_BLOOD_SUGAR_URL, entity, String.class);
        } catch (RestClientException e) {
            log.warn("血糖回写结果确认失败", e);
        }
    }

    private HttpEntity<String> buildHttpEntity(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("PdiToken", BLOOD_SUGAR_TOKEN);
        headers.add("content-type","application/json");
        return new HttpEntity<>(body, headers);
    }

    class ResponseMessage {
        private int code;
        private String message;
        private int totalCount;
        private List<JsonObject> data;

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<JsonObject> getData() {
            return data;
        }

        public void setData(List<JsonObject> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "ResponseMessage{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    ", totalCount=" + totalCount +
                    ", data=" + data +
                    '}';
        }
    }
}