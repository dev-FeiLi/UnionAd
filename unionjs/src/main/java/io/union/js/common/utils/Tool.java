package io.union.js.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/7/14.
 */
public class Tool {
    private final static Logger logger = LoggerFactory.getLogger(Tool.class);

    public static String obtainRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("PRoxy-Client-IPLocation");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IPLocation");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.hasText(ip)) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

    public static String host(String url) {
        if (null == url) {
            return "";
        }
        url = url.toLowerCase();
        url = url.replaceAll("http://", "");
        url = url.replaceAll("https://", "");
        int index = url.indexOf("/");
        if (index > 0) {
            url = url.substring(0, index);
        }
        return url;
    }

    /**
     * 判断字符是否是中文
     *
     * @param c 字符
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
        try {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                return true;
            }
        } catch (Exception e) {
            logger.error("ischinese error: ", e);
        }
        return false;
    }

    /**
     * 判断字符串是否是乱码
     *
     * @param strName 字符串
     * @return 是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        try {
            char[] ch = strName.trim().toCharArray();
            for (int i = 0; i < ch.length; i++) {
                if (!Character.isISOControl(ch[i])) {
                    if (!isChinese(ch[i])) {
                        if (!Character.isLetterOrDigit(ch[i])) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("isMessy error: ", e);
        }
        return false;
    }

    /**
     * 判断蜘蛛
     *
     * @param userAgent
     * @return
     */
    public static boolean isSpider(String userAgent) {
        try {
            userAgent = userAgent.toLowerCase();
            if (userAgent.contains("spider")) {
                return true;
            }
        } catch (Exception e) {
            logger.error("is spider error: " + e);
        }
        return false;
    }
}
