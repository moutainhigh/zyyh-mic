package com.jkys.zyyh.mic.common.enums;

import java.util.Objects;

/**
 * 枚举是否常量
 */
public enum BooleanFlagEnum {
    NO(0, false, "否"),
    YES(1, true, "是");

    private Integer value;
    private Boolean flag;
    private String message;

    BooleanFlagEnum(Integer value, Boolean flag, String message) {
        this.value = value;
        this.flag = flag;
        this.message = message;
    }


    public static BooleanFlagEnum getByValue(Integer value) {
        if (Objects.isNull(value)) {
            return null;
        }
        for (BooleanFlagEnum enu : values()) {
            if (value.equals(enu.getValue())) {
                return enu;
            }
        }
        return null;
    }

    public static BooleanFlagEnum getByFlag(Boolean flag) {
        if (Objects.isNull(flag)) {
            return null;
        }
        for (BooleanFlagEnum enu : values()) {
            if (flag.equals(enu.getFlag())) {
                return enu;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public Boolean getFlag() {
        return flag;
    }

    public String getMessage() {
        return message;
    }
}
