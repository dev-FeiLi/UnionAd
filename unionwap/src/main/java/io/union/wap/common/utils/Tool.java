package io.union.wap.common.utils;

import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/7/14.
 */
public class Tool {

    public static String obtainRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("PRoxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.hasText(ip)) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

    public static String base64encode(String s) throws Exception {
        if (null == s) {
            return null;
        }
        byte[] bytes = Base64Utils.encode(s.getBytes("utf-8"));
        return new String(bytes);
    }

    public static String base64decode(String s) throws Exception {
        if (null == s) {
            return null;
        }
        byte[] bytes = Base64Utils.decode(s.getBytes("utf-8"));
        return new String(bytes);
    }

    public static String host(String url) {
        if (null == url) {
            return url;
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
}
