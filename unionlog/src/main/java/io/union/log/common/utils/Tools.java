package io.union.log.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tools {
    static final Logger logger = LoggerFactory.getLogger(Tools.class);

    // 有些字段在索引中，有些是为了节省存储，在存入数据库之前，先截断
    public static String cutdown(String org) {
        try {
            if (null != org) {
                return org.length() > 200 ? org.substring(0, 200) : org;
            }
        } catch (Exception e) {
            logger.error("cut string: " + org);
            logger.error("cut error: ", e);
        }
        return "";
    }
}
