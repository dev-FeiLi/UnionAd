package io.union.log.common.utils;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/7/31.
 */
public class StatHourTool<E> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public E hour(E clazz, int hour, Object num) {
        try {
            Method getmethod = clazz.getClass().getMethod("getHour" + hour);
            Object oldobj = getmethod.invoke(clazz);
            if (num instanceof Long || num instanceof Integer) {
                Long oldvalue = 0L, newvalue = Long.valueOf(String.valueOf(num));
                if (null != oldobj) {
                    oldvalue = Long.valueOf(String.valueOf(oldobj));
                }
                Method setmethod = clazz.getClass().getMethod("setHour" + hour, Long.class);
                setmethod.invoke(clazz, oldvalue + newvalue);
            } else if (num instanceof Double || num instanceof Float) {
                String oldvalue = "0.0", newvalue = String.valueOf(num);
                if (num != oldobj) {
                    oldvalue = String.valueOf(oldobj);
                }
                Method setmethod = clazz.getClass().getMethod("setHour" + hour, Double.class);
                setmethod.invoke(clazz, NumberUtils.createBigDecimal(oldvalue).add(NumberUtils.createBigDecimal(newvalue)).doubleValue());
            }
        } catch (Exception e) {
            logger.error("set error: ", e);
        }
        return clazz;
    }

    public <T> T poll(E clazz, int hour, Class<T> type, T value) {
        try {
            Method getmethod = clazz.getClass().getMethod("getHour" + hour);
            Object oldobj = getmethod.invoke(clazz);
            T result = null;
            if (null != oldobj) {
                if (value instanceof Long) {
                    result = type.cast(Long.valueOf(String.valueOf(oldobj)));
                } else if (value instanceof Double) {
                    result = type.cast(Double.valueOf(String.valueOf(oldobj)));
                } else if (value instanceof Integer) {
                    result = type.cast(Integer.valueOf(String.valueOf(oldobj)));
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("poll error: ", e);
        }
        return null;
    }
}
