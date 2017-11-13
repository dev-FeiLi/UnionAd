package io.union.admin.common.utils;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/7/31.
 */
public class StatHourTool<E> {

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
                Double oldvalue = 0.0, newvalue = Double.valueOf(String.valueOf(num));
                if (num != oldobj) {
                    oldvalue = Double.valueOf(String.valueOf(oldobj));
                }
                Method setmethod = clazz.getClass().getMethod("setHour" + hour, Double.class);
                setmethod.invoke(clazz, oldvalue + newvalue);
            }
        } catch (Exception e) {

        }
        return clazz;
    }
}
