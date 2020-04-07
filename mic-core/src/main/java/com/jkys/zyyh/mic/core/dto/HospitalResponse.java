package com.jkys.zyyh.mic.core.dto;

import lombok.Data;

/**
 * Hospital系统返回结果.
 *
 * @author: zhuwenjie
 * @datetime: 2020-01-17 14:08
 */
@Data
public class HospitalResponse<T> {
    private int code;
    private String message;
    private T data;
}
