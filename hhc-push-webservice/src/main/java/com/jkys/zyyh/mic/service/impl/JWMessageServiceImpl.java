package com.jkys.zyyh.mic.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
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
    private static String REGISTER_BLOOD_SUGAR_URL = "http://localhost:9101/services/message";

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
    private static String begin = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:urn=\"urn:hl7-org:v3\">\n" +
            "   <soap:Header/>\n" +
            "   <soap:Body>\n" +
            "      <urn:test>\n" +
            "         <!--Optional:-->\n" +
            "         <message><![CDATA[";
    private static String end = "]]></message>\n" +
            "      </urn:test>\n" +
            "   </soap:Body>\n" +
            "</soap:Envelope>";
    private static String REGISTER_BLOOD_SUGAR_BODY = begin + "<RCMR_IN000002UV02 ITSVersion=\"XML_1.0\" xmlns=\"urn:hl7-org:v3\"\n" +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"urn:hl7-org:v3 \n" +
            "file:///E:/hl7/HL7/v3ballot_fullsite_2011MAY/v3ballot/html/processable/multicacheschemas/RCMR\n" +
            "_IN000002UV02.xsd\">\n" +
            "\t<!--id-消息流水号-->\n" +
            "\t<id extension=\"%s\"/>\n" +
            "\t<!--creationTime-消息创建时间-->\n" +
            "\t<creationTime value=\"%s\"/>\n" +
            "\t<!--interactionId-消息的服务标识-->\n" +
            "\t<interactionId root=\"2.16.840.1.113883.1.6\" extension=\"RCMR_IN000002UV02\"/>\n" +
            "\t<!--processingCode-处理代码。标识此消息是否是产品、训练、调试系统的一部分。D：调试；P：产品；T：训练-->\n" +
            "\t<processingCode code=\"P\"/>\n" +
            "\t<!--processingModeCode-处理模型代码。定义此消息是一个文档处理还是一个初始装载的一部分。A：存档；I：初始装载；R：从存档中恢复；T：当前处理，间隔传递。-->\n" +
            "\t<processingModeCode/>\n" +
            "\t<!--acceptAckCode-接收确认类型 AL：总是确认；NE：从不确认；ER：仅在错误/或拒绝时确认；SU：仅在成功完成时确认。-->\n" +
            "\t<acceptAckCode code=\"AL\"/>\n" +
            "\t<receiver typeCode=\"RCV\">\n" +
            "\t\t<device classCode=\"DEV\" determinerCode=\"INSTANCE\">\n" +
            "\t\t\t<id>\n" +
            "\t\t\t\t<item extension=\"1111\"/>\n" +
            "\t\t\t</id>\n" +
            "\t\t</device>\n" +
            "\t</receiver>\n" +
            "\t<sender typeCode=\"SND\">\n" +
            "\t\t<device classCode=\"DEV\" determinerCode=\"INSTANCE\">\n" +
            "\t\t\t<id>\n" +
            "\t\t\t\t<item extension=\"222\"/>\n" +
            "\t\t\t</id>\n" +
            "\t\t</device>\n" +
            "\t</sender>\n" +
            "\t<controlActProcess classCode=\"STC\" moodCode=\"APT\">\n" +
            "\t\t<code code=\"EMRDocumentRegister\">\n" +
            "\t\t\t<displayName value=\"电子病历文档注册\"/>\n" +
            "\t\t</code>\n" +
            "\t\t<subject typeCode=\"SUBJ\">\n" +
            "\t\t\t<clinicalDocument classCode=\"DOCCLIN\" moodCode=\"EVN\">\n" +
            "\t\t\t\t<!--文档流水号-->\n" +
            "\t\t\t\t<id>\n" +
            "\t\t\t\t\t<item root=\"2.16.156.10011.1.1\" extension=\"22\"/>\n" +
            "\t\t\t\t</id>\n" +
            "\t\t\t\t<!--文档类型代码-->\n" +
            "\t\t\t\t<code code=\"C0001\" codeSystem=\"2.16.156.10011.2.4\" codeSystemName=\"卫生信息共享文档编码体系\">\n" +
            "\t\t\t\t\t<displayName value=\"病历概要\"/>\n" +
            "\t\t\t\t</code>\n" +
            "\t\t\t\t<statusCode/>\n" +
            "\t\t\t\t<!--文档生成日期时间-->\n" +
            "\t\t\t\t<effectiveTime value=\"%s\"/>\n" +
            "\t\t\t\t<!--文档保密级别-->\n" +
            "\t\t\t\t<confidentialityCode code=\"N\">\n" +
            "\t\t\t\t\t<displayName value=\"正常访问保密级别\"/>\n" +
            "\t\t\t\t</confidentialityCode>\n" +
            "\t\t\t\t<!--文档版本号-->\n" +
            "\t\t\t\t<versionNumber value=\"1\"/>\n" +
            "\t\t\t\t<storageCode>\n" +
            "\t\t\t\t\t<!--经base64编码的文档原始内容-->\n" +
            "\t\t\t\t\t<originalText value=\"%s\"/>\n" +
            "\t\t\t\t\t<!--文档格式-->\n" +
            "\t\t\t\t\t<translation/>\n" +
            "\t\t\t\t</storageCode>\n" +
            "\t\t\t\t<recordTarget typeCode=\"RCT\">\n" +
            "\t\t\t\t\t<patient classCode=\"PAT\">\n" +
            "\t\t\t\t\t\t<id>\n" +
            "\t\t\t\t\t\t\t<!--PatientID-->\n" +
            "\t\t\t\t\t\t\t<item extension=\"%s\"/>\n" +
            "\t\t\t\t\t\t\t<!--住院号标识 -->\n" +
            "\t\t\t\t\t\t\t<item root=\"2.16.156.10011.1.12\" extension=\"%s\"/>\n" +
            "\t\t\t\t\t\t\t<!--门诊号标识 -->\n" +
            "\t\t\t\t\t\t\t<item root=\"2.16.156.10011.1.11\" extension=\"11\"/>\n" +
            "\t\t\t\t\t\t</id>\n" +
            "\t\t\t\t\t\t<statusCode/>\n" +
            "\t\t\t\t\t\t<!--患者就诊日期时间-->\n" +
            "\t\t\t\t\t\t<effectiveTime>\n" +
            "\t\t\t\t\t\t\t<low value=\"%s\"/>\n" +
            "\t\t\t\t\t\t\t<high/>\n" +
            "\t\t\t\t\t\t</effectiveTime>\n" +
            "\t\t\t\t\t\t<patientPerson classCode=\"PSN\" determinerCode=\"INSTANCE\">\n" +
            "\t\t\t\t\t\t\t<!--身份证号-->\n" +
            "\t\t\t\t\t\t\t<id>\n" +
            "\t\t\t\t\t\t\t\t<item root=\"2.16.156.10011.1.3\" extension=\"%s\"/>\n" +
            "\t\t\t\t\t\t\t</id>\n" +
            "\t\t\t\t\t\t\t<!--姓名-->\n" +
            "\t\t\t\t\t\t\t<name xsi:type=\"DSET_EN\">\n" +
            "\t\t\t\t\t\t\t\t<item>\n" +
            "\t\t\t\t\t\t\t\t\t<part value=\"%s\"/>\n" +
            "\t\t\t\t\t\t\t\t</item>\n" +
            "\t\t\t\t\t\t\t</name>\n" +
            "\t\t\t\t\t\t</patientPerson>\n" +
            "\t\t\t\t\t\t<providerOrganization classCode=\"ORG\" determinerCode=\"INSTANCE\">\n" +
            "\t\t\t\t\t\t\t<id>\n" +
            "\t\t\t\t\t\t\t\t<item root=\"2.16.156.10011.1.5\" extension=\"XXXXX\"/>\n" +
            "\t\t\t\t\t\t\t</id>\n" +
            "\t\t\t\t\t\t\t<name xsi:type=\"DSET_EN\">\n" +
            "\t\t\t\t\t\t\t\t<item>\n" +
            "\t\t\t\t\t\t\t\t\t<part value=\"宁夏回族自治区第五人民医院\"/>\n" +
            "\t\t\t\t\t\t\t\t</item>\n" +
            "\t\t\t\t\t\t\t</name>\n" +
            "\t\t\t\t\t\t\t<!--科室标识-->\n" +
            "\t\t\t\t\t\t\t<organizationContains classCode=\"PART\">\n" +
            "\t\t\t\t\t\t\t\t<id>\n" +
            "\t\t\t\t\t\t\t\t\t<item root=\"2.16.156.10011.1.26\" extension=\"%s\"/>\n" +
            "\t\t\t\t\t\t\t\t</id>\n" +
            "\t\t\t\t\t\t\t</organizationContains>\n" +
            "\t\t\t\t\t\t</providerOrganization>\n" +
            "\t\t\t\t\t</patient>\n" +
            "\t\t\t\t</recordTarget>\n" +
            "\t\t\t\t<!--文档创建者-->\n" +
            "\t\t\t\t<author typeCode=\"AUT\">\n" +
            "\t\t\t\t\t<time/>\n" +
            "\t\t\t\t\t<assignedAuthor classCode=\"ASSIGNED\">\n" +
            "\t\t\t\t\t\t<id>\n" +
            "\t\t\t\t\t\t\t<item root=\"2.16.156.10011.1.4\" extension=\"%s\"/>\n" +
            "\t\t\t\t\t\t</id>\n" +
            "\t\t\t\t\t\t<assignedPerson classCode=\"PSN\" determinerCode=\"INSTANCE\">\n" +
            "\t\t\t\t\t\t\t<name xsi:type=\"DSET_EN\">\n" +
            "\t\t\t\t\t\t\t\t<item>\n" +
            "\t\t\t\t\t\t\t\t\t<part value=\"%s\"/>\n" +
            "\t\t\t\t\t\t\t\t</item>\n" +
            "\t\t\t\t\t\t\t</name>\n" +
            "\t\t\t\t\t\t</assignedPerson>\n" +
            "\t\t\t\t\t</assignedAuthor>\n" +
            "\t\t\t\t</author>\n" +
            "\t\t\t\t<!--文档保管单位-->\n" +
            "\t\t\t\t<custodian typeCode=\"CST\">\n" +
            "\t\t\t\t\t<assignedCustodian classCode=\"ASSIGNED\">\n" +
            "\t\t\t\t\t\t<representedOrganization classCode=\"ORG\" determinerCode=\"INSTANCE\">\n" +
            "\t\t\t\t\t\t\t<id>\n" +
            "\t\t\t\t\t\t\t\t<item root=\"2.16.156.10011.1.5\" extension=\"XXXXX\"/>\n" +
            "\t\t\t\t\t\t\t</id>\n" +
            "\t\t\t\t\t\t\t<name xsi:type=\"DSET_EN\">\n" +
            "\t\t\t\t\t\t\t\t<item>\n" +
            "\t\t\t\t\t\t\t\t\t<part value=\"宁夏回族自治区第五人民医院\"/>\n" +
            "\t\t\t\t\t\t\t\t</item>\n" +
            "\t\t\t\t\t\t\t</name>\n" +
            "\t\t\t\t\t\t</representedOrganization>\n" +
            "\t\t\t\t\t</assignedCustodian>\n" +
            "\t\t\t\t</custodian>\n" +
            "\t\t\t</clinicalDocument>\n" +
            "\t\t</subject>\n" +
            "\t</controlActProcess>\n" +
            "</RCMR_IN000002UV02>" + end;
    private static String REGISTER_BLOOD_SUGAR_SUCCESS = "<MCCI_IN000002UV01 ITSVersion=\"XML_1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "xmlns=\"urn:hl7-org:v3\" xsi:schemaLocation=\"urn:hl7-org:v3 \n" +
            "file:///E:/hl7/HL7/v3ballot_fullsite_2011MAY/v3ballot/html/processable/multicacheschemas/MCCI\n" +
            "_IN000002UV01.xsd\">\n" +
            "<id extension=\"@12122\"/>\n" +
            "<creationTime value=\"20170106151903\"/>\n" +
            "<interactionId root=\"2.16.840.1.113883.1.6\" extension=\"MCCI_IN000002UV01\"/>\n" +
            "<processingCode code=\"P\"/>\n" +
            "WS/T XXXXX.6—XXXX\n" +
            "19\n" +
            "<processingModeCode/>\n" +
            "<acceptAckCode code=\"AL\"/>\n" +
            "<receiver typeCode=\"RCV\">\n" +
            "<device classCode=\"DEV\" determinerCode=\"INSTANCE\">\n" +
            "<id><item extension=\"@111\"/>\n" +
            "</id>\n" +
            "</device>\n" +
            "</receiver> <sender typeCode=\"SND\">\n" +
            "<device classCode=\"DEV\" determinerCode=\"INSTANCE\">\n" +
            "<id><item extension=\"@222\"/>\n" +
            "</id>\n" +
            "</device>\n" +
            "</sender> <acknowledgement typeCode=\"AA\">\n" +
            "<!--请求消息ID--> <targetMessage> <id extension=\"请求的消息ID\"/>\n" +
            "</targetMessage> <acknowledgementDetail> <text value=\"处理结果说明\"/>\n" +
            "</acknowledgementDetail>\n" +
            "</acknowledgement>\n" +
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
    public String test(String message) {

        System.out.println("接收到消息：" + message);

        return REGISTER_BLOOD_SUGAR_SUCCESS;
    }

    @Scheduled(cron = "${hissynctask.callback.cron:0 0/2 * * * ?}")
    public void bloodSugarCallBack() {
        // 调用 pdi 血糖回写的接口获取血糖回写数据
        ResponseMessage response = getBloodSugar();

        // 遍历血糖数据模型数据，可以通过 bloodSugarModel.get("id").getAsString() 获取数据，并进行处理
        // 这里使用 字符串拼接 + 字符串填充的方式组成 回写信息
        // 因为只能一次接收一条，只能遍历并发送请求了
        JsonArray confirmIds = new JsonArray();
        for (JsonObject bloodSugarModel : response.getData()) {
            log.info("血糖回写数据：[{}]", AGSON.toJSONString(bloodSugarModel));
            try {
                StringBuffer buffer = new StringBuffer();
                buffer.append("返回数据");
                buffer.append(bloodSugarModel.get("id").getAsString());

                // 加密 血糖数据
                // 这里有两种方式，一种是去除换行符，一种是没有去除换行符
                System.out.println(XmlUtil.getBase64(buffer.toString()).replace("\r\n", ""));
                System.out.println(XmlUtil.getBase64(buffer.toString()));

                // 传入加密的文档内容,拼接出最终回写的xml字符串
                String body = getRegisterBloodSugarBody(bloodSugarModel, "");

                // 拼接注册 xml
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.parseMediaType("text/xml;charset=UTF-8"));
                // REGISTER_BLOOD_SUGAR_URL 对方接口的地址
                String responseEntity = restTemplate.postForObject(REGISTER_BLOOD_SUGAR_URL, new HttpEntity<String>(body, httpHeaders), String.class);

                // 判断成功的条件，返回的 xml 中 AA 代表成功
                if (responseEntity.contains("AA")) {
                    JsonObject confirmId = new JsonObject();
                    confirmId.add("id", bloodSugarModel.get("id"));
                    confirmIds.add(confirmId);
                    log.info("血糖回写处理成功 [{}]", bloodSugarModel.get("id").getAsString());
                }
            } catch (Exception e) {
                log.warn("回写血糖处理失败", e);
            }
        }

        // 调用 pdi 回写确认接口，改变已经回写的血糖数据的状态
        if (confirmIds.size() != 0) {
            log.info("血糖回写结果确认 {}", AGSON.toJSONString(confirmIds));
            confirmBloodSugar(confirmIds);
        }
    }

    private String getRegisterBloodSugarBody(JsonObject bloodSugarModel, String encodeDocument) {
        String id = bloodSugarModel.get("id").getAsString();
        Date date = new Date();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String messageCreateTime = dateTimeFormat.format(date);
        String documentCreateTime = dateFormat.format(date);
        // 加密的文档报告
        String encodeText = encodeDocument;
        String hisPatientId = bloodSugarModel.get("hisPatientId").getAsString();
        String inpatientId = bloodSugarModel.get("inpatientNo").getAsString();
        String operationTime = dateFormat.format(new Date(bloodSugarModel.get("operateTime").getAsLong()));
        String idCard = "";
        String name = bloodSugarModel.get("name").getAsString();
        // 对于可能会空的值进行判空处理
        JsonElement deptCode = bloodSugarModel.get("hisDeptCode");
        JsonElement operationId = bloodSugarModel.get("operatorId");
        JsonElement operationName = bloodSugarModel.get("operatorName");


        return String.format(REGISTER_BLOOD_SUGAR_BODY,
                id, messageCreateTime, documentCreateTime, encodeText, hisPatientId,
                inpatientId, operationTime, idCard, name,
                // 判空处理
                deptCode.isJsonNull() ? "" : deptCode.getAsString(),
                operationId.isJsonNull() ? "" : operationId.getAsString(),
                operationName.isJsonNull() ? "" : operationName.getAsString());
    }

    private ResponseMessage getBloodSugar() {
        HttpEntity<String> entity = buildHttpEntity(null);
        ResponseEntity<String> responseEntity = restTemplate.exchange(BLOOD_SUGAR_URL, HttpMethod.GET, entity, String.class);
        String body = responseEntity.getBody();
        return AGSON.parseObject(body, ResponseMessage.class);
    }

    private void confirmBloodSugar(JsonArray confirmIds) {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("data", confirmIds);
            HttpEntity<String> entity = buildHttpEntity(AGSON.toJSONString(jsonObject));
            restTemplate.postForEntity(CONFIRM_BLOOD_SUGAR_URL, entity, String.class);
        } catch (RestClientException e) {
            log.warn("血糖回写结果确认失败", e);
        }
    }

    private HttpEntity<String> buildHttpEntity(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("PdiToken", BLOOD_SUGAR_TOKEN);
        headers.add("content-type", "application/json");
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