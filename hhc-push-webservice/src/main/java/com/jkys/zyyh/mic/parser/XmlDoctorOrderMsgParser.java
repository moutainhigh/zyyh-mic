package com.jkys.zyyh.mic.parser;

import com.jkys.hhc.conn.model.DoctorOrderModel;
import com.jkys.zyyh.mic.api.common.HisBizMessage;
import com.jkys.zyyh.mic.api.common.HisMessageContext;
import com.jkys.zyyh.mic.api.service.DoctorOrderListParser;
import com.jkys.zyyh.mic.utils.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName XmlDoctorOrderMsgParser
 * @Description 医嘱解析器
 * @Author Gabriel
 * @Date 2020/4/3 11:30
 * @Version V1.0
 */
@Service
@Slf4j
public class XmlDoctorOrderMsgParser implements DoctorOrderListParser {

    /**
     * 定时读取数据库中信息，然后处理
     *
     * @param hisMessageContext 这里面的 message 还是 xml 数据
     * @return
     */
    @Override
    public List<DoctorOrderModel> apply(HisMessageContext hisMessageContext) {
        HisBizMessage hisBizMessage = hisMessageContext.getHisBizMessage();
        List<DoctorOrderModel> doctorOrderModels = parserXml(hisBizMessage.getContent());
        return doctorOrderModels;
    }

    private List<DoctorOrderModel> parserXml(String xmlStr) {
        List<DoctorOrderModel> doctorOrderModels = new ArrayList<>();
        DoctorOrderModel doctorOrderModel = new DoctorOrderModel();
        Document document = XmlUtil.getDocument(xmlStr);

        try {
            String orderTime = XmlUtil.getValueByXpath(document, "");
            String startTime = XmlUtil.getValueByXpath(document, "//controlActProcess/subject/placerGroup/component2/substanceAdministrationRequest/effectiveTime/low/@value");
            String endTime = XmlUtil.getValueByXpath(document, "//controlActProcess/subject/placerGroup/component2/substanceAdministrationRequest/effectiveTime/high/@value");
            doctorOrderModel.setDeptCode(XmlUtil.getValueByXpath(document, "//controlActProcess/subject/placerGroup/component2/substanceAdministrationRequest/location/serviceDeliveryLocation/location/id/item/@extension"));
            doctorOrderModel.setWardCode(XmlUtil.getValueByXpath(document, "//controlActProcess/subject/placerGroup/componentOf1/encounter/location/serviceDeliveryLocation/serviceProviderOrganization/asOrganizationPartOf/wholeOrganization/id/item/@extension"));
            doctorOrderModel.setOrderNo(XmlUtil.getValueByXpath(document, "//controlActProcess/subject/placerGroup/component2/substanceAdministrationRequest/id/@extension"));
            doctorOrderModel.setOrderSubNo(XmlUtil.getValueByXpath(document, "//controlActProcess/subject/placerGroup/component2/sequenceNumber/@value"));
            doctorOrderModel.setFrequencyCode(XmlUtil.getValueByXpath(document, "//controlActProcess/subject/placerGroup/component2/substanceAdministrationRequest/effectiveTime/code/@code"));
            doctorOrderModel.setHisPatientId(XmlUtil.getValueByXpath(document, "//controlActProcess/subject/placerGroup/componentOf1/encounter/subject/patient/id/item/@extension"));
            doctorOrderModel.setInpatientNo(XmlUtil.getValueByXpath(document, "//controlActProcess/subject/placerGroup/componentOf1/encounter/subject/patient/id/item/@extension"));
            doctorOrderModel.setOrderTime(StringUtils.isNotBlank(orderTime) ? DateUtils.parseDate(orderTime, "yyyyMMddHHmmss") : null);
            doctorOrderModel.setStartTime(StringUtils.isNotBlank(startTime) ? DateUtils.parseDate(startTime, "yyyyMMddHHmmss") : null);
            doctorOrderModel.setEndTime(StringUtils.isNotBlank(endTime) ? DateUtils.parseDate(endTime, "yyyyMMddHHmmss") : null);

            doctorOrderModel.setSource(3);
            doctorOrderModel.setLongFlag(Integer.valueOf(Optional.ofNullable(XmlUtil.getValueByXpath(document, "//controlActProcess/subject/placerGroup/component2/\n" +
                    "substanceAdministrationRequest/pertinentInformation/observation/value/@code")).orElse("0")));
            doctorOrderModel.setExecuteStatus(Integer.valueOf(Optional.ofNullable(XmlUtil.getValueByXpath(document, "")).orElse("3")));
        } catch (Exception e) {
            log.warn("填充医嘱 model 失败",e);
        }
        doctorOrderModels.add(doctorOrderModel);

        return doctorOrderModels;
    }
}