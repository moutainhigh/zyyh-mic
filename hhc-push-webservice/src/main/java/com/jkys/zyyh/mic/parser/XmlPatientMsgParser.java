package com.jkys.zyyh.mic.parser;

import com.jkys.hhc.conn.model.PatientModel;
import com.jkys.zyyh.mic.api.common.HisBizMessage;
import com.jkys.zyyh.mic.api.common.HisMessageContext;
import com.jkys.zyyh.mic.api.service.PatientMsgParser;
import com.jkys.zyyh.mic.utils.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.text.ParseException;
import java.util.Optional;

/**
 * @ClassName XmlPatientMsgParser
 * @Description 病人解析器
 * @Author Gabriel
 * @Date 2020/4/3 11:29
 * @Version V1.0
 */
@Service
@Slf4j
public class XmlPatientMsgParser implements PatientMsgParser {


    @Override
    public PatientModel apply(HisMessageContext hisMessageContext) {
        HisBizMessage hisBizMessage = hisMessageContext.getHisBizMessage();
        PatientModel patientModel = parserXml(hisBizMessage.getContent());
        return patientModel;
    }

    private PatientModel parserXml(String xmlStr){
        PatientModel patientModel = new PatientModel();
        Document document = XmlUtil.getDocument(xmlStr);

        try {
            patientModel.setInpatientNo(XmlUtil.getValueByXpath(document,"//controlActProcess/subject/encounterEvent/id/item/@extension"));
            String inDay = XmlUtil.getValueByXpath(document, "//controlActProcess/subject/encounterEvent/effectiveTime/low/@value");
            String inDeptDay = XmlUtil.getValueByXpath(document,"//controlActProcess/subject/encounterEvent/location1/time/low/@value");
            patientModel.setInDay(StringUtils.isNotBlank(inDay) ? DateUtils.parseDate(inDay,"yyyyMMdd") : null);
            patientModel.setInDeptTime(StringUtils.isNotBlank(inDeptDay) ? DateUtils.parseDate(inDeptDay,"yyyyMMdd") : null);
            patientModel.setAdmissionTimes(Integer.valueOf(Optional.ofNullable(XmlUtil.getValueByXpath(document,"//controlActProcess/subject/encounterEvent/lengthOfStayQuantity/@value")).orElse("0")));
            patientModel.setHisPatientId(XmlUtil.getValueByXpath(document,"//controlActProcess/subject/encounterEvent/subject/patient/id/item/@extension"));
            patientModel.setIdCard(XmlUtil.getValueByXpath(document,"//controlActProcess/subject/encounterEvent/subject/patient/patientPerson/id/item/@extension"));
            patientModel.setName(XmlUtil.getValueByXpath(document,"//controlActProcess/subject/encounterEvent/subject/patient/patientPerson/name/item/part/@value"));
            patientModel.setDoctorCode(XmlUtil.getValueByXpath(document,"//controlActProcess/subject/encounterEvent/admitter/assignedPerson/id/item/@extension"));
            patientModel.setDoctorName(XmlUtil.getValueByXpath(document,"//controlActProcess/subject/encounterEvent/admitter/assignedPerson/assignedPerson/name/item/part/@value"));
            String deptCode = XmlUtil.getValueByXpath(document, "//controlActProcess/subject/encounterEvent/location/serviceDeliveryLocation/location/id/item/@extension");
            String transDeptCode = XmlUtil.getValueByXpath(document,"//controlActProcess/subject/encounterEvent/location1/serviceDeliveryLocation/location/id/item/@extension");
            patientModel.setDeptCode(StringUtils.isNotBlank(deptCode) ?deptCode:transDeptCode);
            String wardCode = XmlUtil.getValueByXpath(document, "//controlActProcess/subject/encounterEvent/location/serviceDeliveryLocation/location/locatedEntityHasParts/locatedPlace/id/item/@extension");
            String transWardCode = XmlUtil.getValueByXpath(document,"//controlActProcess/subject/encounterEvent/location1/serviceDeliveryLocation/location/locatedEntityHasParts/locatedPlace/id/item/@extension");
            patientModel.setWardCode(StringUtils.isNotBlank(wardCode) ?wardCode:transWardCode);
            String bedNumber = XmlUtil.getValueByXpath(document, "//controlActProcess/subject/encounterEvent/location/serviceDeliveryLocation/location/locatedEntityHasParts/locatedPlace/locatedEntityHasParts/locatedPlace/locatedEntityHasParts/locatedPlace/id/item/@extension");
            String transBedNumber = XmlUtil.getValueByXpath(document,"//controlActProcess/subject/encounterEvent/location1/serviceDeliveryLocation/location/locatedEntityHasParts/locatedPlace/locatedEntityHasParts/locatedPlace/locatedEntityHasParts/locatedPlace/id/item/@extension");
            patientModel.setBedNumber(StringUtils.isNotBlank(bedNumber) ?bedNumber:transBedNumber);

            String outDay = XmlUtil.getValueByXpath(document, "//controlActProcess/subject/encounterEvent/effectiveTime/high/@value");
            String outDeptDay = XmlUtil.getValueByXpath(document,"//controlActProcess/subject/encounterEvent/location2/time/low/@value");
            patientModel.setOutDay(StringUtils.isNotBlank(outDay) ? DateUtils.parseDate(outDay,"yyyyMMdd") : null);
            patientModel.setOutDeptDay(StringUtils.isNotBlank(outDeptDay) ? DateUtils.parseDate(outDeptDay,"yyyyMMdd") : null);
        } catch (ParseException e) {
            log.warn("填充病人 Model 失败",e);
        }
        return patientModel;
    }

}