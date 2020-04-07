package com.jkys.zyyh.mic.server.common;

import java.io.Serializable;

/**
 * <Description> <br>
 *
 * @author Rocky<br>
 * @version 1.0<br>
 * @createDate 2019/11/01 10:48 上午 <br>
 */
public class ApiResult <T> implements Serializable {

    private static final long serialVersionUID = -2420994155211715217L;

    private String message = "成功";

    private String code = "200";

    private boolean success = true;

    private T data;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ApiResult createResult(T data){
        ApiResult apiResult = new ApiResult();
        apiResult.setData(data);
        return apiResult;
    }
}
