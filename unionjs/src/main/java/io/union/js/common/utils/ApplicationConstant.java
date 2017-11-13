package io.union.js.common.utils;

/**
 * Created by Administrator on 2017/7/20.
 */
public class ApplicationConstant {

    public final static String ERROR_PAGE_PATH = "/error"; // 系统错误路径

    public final static String REQUEST_COUNT_TOPIC = "unionreqtstopic"; // JS请求计数在kafka中的topic
    public final static String CLICK_COUNT_TOPIC = "unionclicklogtopic"; // 点击日志在kafka中的topic
    public final static String PV_COUNT_TOPIC = "unionpvlogtopic"; // PV日志在kafka中的topic
    public final static String EFFECT_COUNT_TOPIC = "effectlogtopic"; // 效果跟踪在kafka中的topic
}
