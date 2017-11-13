package io.union.wap.common.utils;

/**
 * Created by Administrator on 2017/7/20.
 */
public class ApplicationConstant {

    public final static String ERROR_PAGE_PATH = "/error"; // 系统错误路径
    public final static String AES_SECRET_KEY = "1749d8a67603919b688072f3e74e8c0c"; // AES加密key
    public final static String SESSION_USER_CONTEXT = "userContext"; // 会员存在session的key
    public final static String SESSION_SUC_MSG = "successmsg"; // session中成功消息的key
    public final static String SESSION_ERR_MSG = "errmsg"; // session中错误信息的key


    public final static String REQUEST_COUNT_TOPIC = "unionreqtstopic"; // JS请求计数在kafka中的topic
    public final static String REQUEST_COUNT_GROUP = "unionreqtsgroup"; // JS请求计数在kafka中的group

    public final static String CLICK_COUNT_TOPIC = "unionclicklogtopic"; // 点击日志在kafka中的topic
    public final static String CLICK_COUNT_GROUP = "unionclickloggroup"; // 点击日志在kafka中的group

    public final static String PV_COUNT_TOPIC = "unionpvlogtopic"; // PV日志在kafka中的topic
    public final static String PV_COUNT_GROUP = "unionpvloggroup"; // PV日志在kafka中的group
}
