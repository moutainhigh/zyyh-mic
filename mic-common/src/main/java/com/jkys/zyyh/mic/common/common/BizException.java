package com.jkys.zyyh.mic.common.common;

/**
 * <Description> <br>
 *
 * @author Rocky<br>
 * @version 1.0<br>
 * @createDate 2019/11/01 10:48 上午 <br>
 */
public class BizException extends RuntimeException{

    public BizException(String code) {
        this.code = code;
    }

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public BizException(Throwable cause, String code) {
        super(cause);
        this.code = code;
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code) {
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
