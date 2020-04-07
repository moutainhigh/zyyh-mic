package com.jkys.zyyh.mic.utils;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import sun.misc.BASE64Encoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * @ClassName XmlUtil
 * @Description 工具类
 * @Author Gabriel
 * @Date 2020/4/3 11:15
 * @Version V1.0
 */
public class XmlUtil {

    private static XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

    private static DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    //    通过xpath获取 属性值
    public static String getValueByXpath(Document doc, String xpathStr){
        String s = null;
        try {
            if (StringUtils.isNotBlank(xpathStr)) {
                // 创建XPath对象
                XPath xpath = XPATH_FACTORY.newXPath();
                s = (String) xpath.evaluate(xpathStr, doc, XPathConstants.STRING);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return StringUtils.isNotBlank(s) ? s : null;
    }

    public static Document getDocument(String message){
        Document doc = null;
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(message.getBytes("UTF-8"));

            // 创建Document对象
            DOCUMENT_BUILDER_FACTORY.setValidating(false);
            DocumentBuilder db = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
            doc = db.parse(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return doc;
    }

    public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

}