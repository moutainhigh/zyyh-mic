package com.jkys.zyyh.mic.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * <Description> <br>
 * 脱敏工具类
 * @author Rocky<br>
 * @version 1.0<br>
 * @createDate 2019/11/01 10:48 上午 <br>
 */
public class DesenseUtil {
    public static final int POSITION = 2;
    private static Pattern p1= Pattern.compile("（[\u4E00-\u9FA5]*）有限公司");
    private static Pattern p2= Pattern.compile("有限公司");

    private DesenseUtil() {
    }

    /**
     * 手机号码前三后四脱敏
     *
     * @param mobile
     * @return
     */
    public static String mobileDesense(String mobile) {
        if (StringUtils.isEmpty(mobile) || (mobile.length() != 11)) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 身份证号脱敏
     *
     * @param id
     * @return
     */
    public static String idCardDesense(String id) {
        if (StringUtils.isEmpty(id) || (id.length() < 8)) {
            return id;
        }
        // ：?<=和?=都表示零宽断言，一个匹配后面一个匹配前面
        return id.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*");
    }

    /**
     * 护照号码脱敏
     *
     * @param id
     * @return
     */
    public static String idPassportDesense(String id) {
        if (StringUtils.isEmpty(id) || (id.length() < 8)) {
            return id;
        }
        return id.substring(0, 2) + new String(new char[id.length() - 5]).replace("\0", "*") + id.substring(id.length() - 3);
    }

    /**
     * 网贷平台名称脱敏
     *
     * @param platformName
     * @return
     */
    public static String desensePlatformName(String platformName) {
        StringBuilder builder = new StringBuilder(platformName);
        Matcher m1= p1.matcher(builder.toString());
        if(m1.find() && m1.start() > POSITION) {
            builder.replace(m1.start() - POSITION, m1.start(), "**");
        } else {
            Matcher m2= p2.matcher(builder.toString());
            if (m2.find() && m2.start() > POSITION) {
                builder.replace(m2.start() - POSITION, m2.start(), "**");
            }
        }
        return builder.toString();
    }
}
