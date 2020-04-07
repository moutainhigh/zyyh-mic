package com.jkys.zyyh.mic.common.common;

/**
 * @author twj
 * 2020/1/5 20:10
 */
public class HisBizException extends BizException {
    public HisBizException(String code) {
        super(code);
    }

    public HisBizException(String code, String message) {
        super(code, message);
    }

    public HisBizException(String message, Throwable cause, String code) {
        super(message, cause, code);
    }

    public HisBizException(Throwable cause, String code) {
        super(cause, code);
    }

    public HisBizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code) {
        super(message, cause, enableSuppression, writableStackTrace, code);
    }
}
