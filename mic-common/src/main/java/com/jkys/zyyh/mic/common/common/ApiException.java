package com.jkys.zyyh.mic.common.common;

/**
 * <Description> <br>
 *
 * @author Rocky<br>
 * @version 1.0<br>
 * @createDate 2019/11/01 10:48 上午 <br>
 */
public class ApiException extends RuntimeException{

    public ApiException(String code) {
        this.code = code;
    }

    public ApiException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public ApiException(Throwable cause, String code) {
        super(cause);
        this.code = code;
    }

    public ApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
