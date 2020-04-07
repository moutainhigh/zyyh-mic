package com.jkys.zyyh.mic.common.util;

import com.jkys.zyyh.mic.common.common.BaseCodeEnum;

/**
 * @author twj
 * 2020/1/2 18:23
 */
public class CodeEnumUtil {
    /**
     *
     * @param enumClass 枚举类 class
     * @param code 枚举code
     * @param <E>
     * @return
     */
    public static <E extends Enum<?> & BaseCodeEnum> E codeOf(Class<E> enumClass, int code) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getCode() == code)
                return e;
        }
        return null;
    }
}
