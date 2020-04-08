package com.jkys.zyyh.mic.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @ClassName JWMessageService
 * @Description
 * @Author longchen
 * @Date 2020/4/3 10:06
 * @Version V1.0
 */
@WebService(targetNamespace="urn:hl7-org:v3")
public interface JWMessageService {
    @WebMethod
    String HIPMessageServer(@WebParam(name = "action",targetNamespace = "urn:hl7-org:v3")String action , @WebParam(name = "message",targetNamespace = "urn:hl7-org:v3")String message);

    @WebMethod
    String test(@WebParam(name = "message") String message);

}
