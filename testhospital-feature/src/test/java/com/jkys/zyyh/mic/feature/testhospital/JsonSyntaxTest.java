package com.jkys.zyyh.mic.feature.testhospital;

import com.jkys.agilegson.AGSON;
import com.jkys.hhc.conn.model.PatientModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author twj
 * 2020/2/10 10:10
 */
public class JsonSyntaxTest {
    @Test
    void testName() {
        String text = "{\n" +
                "    \"bizType\": \"PATIENT\",\n" +
                "    \"bizEvent\": \"ADMITTED\",\n" +
                "    \"msg\": \"{\\\"inpatientNo\\\": \\\"HP365\\\", \t\\\"outDay\\\": null, \t\\\"outDeptDay\\\": null, \t\\\"birthday\\\": -440755200000, \t\\\"inDeptTime\\\": \\\"\\\", \t\\\"hisPatientId\\\": \\\"HP824\\\", \t\\\"gender\\\": 1, \t\\\"admissionTimes\\\": 1, \t\\\"inDay\\\": \\\"1581049432263\\\", \t\\\"nurseCode\\\": \\\"HE4000352\\\", \t\\\"name\\\": \\\"病人3875\\\", \t\\\"doctorCode\\\": \\\"HE2000430\\\", \t\\\"wardCode\\\": \\\"HW200013\\\", \t\\\"bedNumber\\\": \\\"A100479\\\", \t\\\"deptCode\\\": \\\"HD200081\\\" }\"\n" +
                "}\n" +
                "\n";
        System.out.println(text);
        TestHospitalRequest testHospitalRequest = AGSON.parseObject(text, TestHospitalRequest.class);

        PatientModel patientModel = AGSON.parseObject(testHospitalRequest.getMsg(), PatientModel.class);
        Assertions.assertNotNull(patientModel);
    }
}
