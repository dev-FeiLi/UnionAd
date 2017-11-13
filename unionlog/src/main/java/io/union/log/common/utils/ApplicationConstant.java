package io.union.log.common.utils;

/**
 * Created by Administrator on 2017/7/20.
 */
public class ApplicationConstant {

    public final static String ERROR_PAGE_PATH = "/error"; // 系统错误路径

    public final static String REQUEST_COUNT_TOPIC = "unionreqtstopic"; // JS请求计数在kafka中的topic
    public final static String REQUEST_COUNT_GROUP = "unionreqtsgroup"; // JS请求计数在kafka中的group

    public final static String CLICK_COUNT_TOPIC = "unionclicklogtopic"; // 点击日志在kafka中的topic
    public final static String CLICK_COUNT_GROUP = "unionclickloggroup"; // 点击日志在kafka中的group

    public final static String PV_COUNT_TOPIC = "unionpvlogtopic"; // PV日志在kafka中的topic
    public final static String PV_COUNT_GROUP = "unionpvloggroup"; // PV日志在kafka中的group

    public final static String AD_SETTLEMENT_TOPIC = "adsettlementtopic"; // 站长广告主结算在kafka中的topic
    public final static String AD_SETTLEMENT_GROUP = "adsettlementgroup"; // 站长广告主结算在kafka中的group

    // ---------------------------处理 log_browser 的kafka ------------------------------------------------
    public final static String LOG_BROWSER_COUNT_TOPIC = "logbrowsercounttopic";
    public final static String LOG_BROWSER_COUNT_GROUP = "logbrowsercountgroup";

    // ----------------------------处理 log_city 的 kafka --------------------------------------------------
    public final static String LOG_CITY_COUNT_TOPIC = "logcitycounttopic";
    public final static String LOG_CITY_COUNT_GROUP = "logcitycountgroup";

    // ----------------------------处理 log_os 的 kafka --------------------------------------------------
    public final static String LOG_OS_COUNT_TOPIC = "logoscounttopic";
    public final static String LOG_OS_COUNT_GROUP = "logoscountgroup";

    // ----------------------------处理 log_screen 的 kafka --------------------------------------------------
    public final static String LOG_SCREEN_COUNT_TOPIC = "logscreencounttopic";
    public final static String LOG_SCREEN_COUNT_GROUP = "logscreencountgroup";

    // ----------------------------处理 temp_ip 的 kafka --------------------------------------------------
    public final static String TEMP_IP_COUNT_TOPIC = "tempipcounttopic";
    public final static String TEMP_IP_COUNT_GROUP = "tempipcountgroup";

    // ----------------------------处理 temp_uv 的 kafka --------------------------------------------------
    public final static String TEMP_UV_COUNT_TOPIC = "tempuvcounttopic";
    public final static String TEMP_UV_COUNT_GROUP = "tempuvcountgroup";

    // ----------------------------处理 temp_cip 的 kafka --------------------------------------------------
    public final static String TEMP_CIP_COUNT_TOPIC = "tempcipcounttopic";
    public final static String TEMP_CIP_COUNT_GROUP = "tempcipcountgroup";

    // ----------------------------处理 temp_cuv 的 kafka --------------------------------------------------
    public final static String TEMP_CUV_COUNT_TOPIC = "tempcuvcounttopic";
    public final static String TEMP_CUV_COUNT_GROUP = "tempcuvcountgroup";

    // ----------------------------处理 stat_hour_money 的 kafka --------------------------------------------------
    public final static String STAT_HOUR_MONEY_COUNT_TOPIC = "hourmoneycounttopic";
    public final static String STAT_HOUR_MONEY_COUNT_GROUP = "hourmoneycountgroup";

    // ----------------------------处理 stat_hour_click 的 kafka --------------------------------------------------
    public final static String STAT_HOUR_CLICK_COUNT_TOPIC = "hourclickcounttopic";
    public final static String STAT_HOUR_CLICK_COUNT_GROUP = "hourclickcountgroup";

    // ----------------------------处理 stat_hour_clickip 的 kafka --------------------------------------------------
    public final static String STAT_HOUR_CLICKIP_COUNT_TOPIC = "hourclickipcounttopic";
    public final static String STAT_HOUR_CLICKIP_COUNT_GROUP = "hourclickipcountgroup";

    // ----------------------------处理 stat_hour_clickuv 的 kafka --------------------------------------------------
    public final static String STAT_HOUR_CLICKUV_COUNT_TOPIC = "hourclickuvcounttopic";
    public final static String STAT_HOUR_CLICKUV_COUNT_GROUP = "hourclickuvcountgroup";

    // ----------------------------处理 stat_hour_ip 的 kafka --------------------------------------------------
    public final static String STAT_HOUR_IP_COUNT_TOPIC = "houripcounttopic";
    public final static String STAT_HOUR_IP_COUNT_GROUP = "houripcountgroup";

    // ----------------------------处理 stat_hour_uv 的 kafka --------------------------------------------------
    public final static String STAT_HOUR_UV_COUNT_TOPIC = "houruvcounttopic";
    public final static String STAT_HOUR_UV_COUNT_GROUP = "houruvcountgroup";

    // ----------------------------处理 stat_hour_pv 的 kafka --------------------------------------------------
    public final static String STAT_HOUR_PV_COUNT_TOPIC = "hourpvcounttopic";
    public final static String STAT_HOUR_PV_COUNT_GROUP = "hourpvcountgroup";

    // ----------------------------处理 stat_hour_reqs 的 kafka --------------------------------------------------
    public final static String STAT_HOUR_REQS_COUNT_TOPIC = "hourreqscounttopic";
    public final static String STAT_HOUR_REQS_COUNT_GROUP = "hourreqscountgroup";

    // ----------------------------处理 log_visit 的 kafka --------------------------------------------------
    public final static String LOG_VISIT_COUNT_TOPIC = "logvisitcounttopic";
    public final static String LOG_VISIT_COUNT_GROUP = "logvisitcountgroup";

    // ----------------------------处理 log_clicks 的 kafka --------------------------------------------------
    public final static String LOG_CLICKS_COUNT_TOPIC = "logclickscounttopic";
    public final static String LOG_CLICKS_COUNT_GROUP = "logclickscountgroup";

    // ----------------------------处理 union_stats 的 kafka --------------------------------------------------
    public final static String UNION_STATS_COUNT_TOPIC = "unionstatscounttopic";
    public final static String UNION_STATS_COUNT_GROUP = "unionstatscountgroup";

    // ----------------------------处理 union_reqts 的 kafka --------------------------------------------------
    public final static String UNION_REQTS_COUNT_TOPIC = "unionreqtscounttopic";
    public final static String UNION_REQTS_COUNT_GROUP = "unionreqtscountgroup";

    // ----------------------------处理 log_effect 的 kafka --------------------------------------------------
    public final static String EFFECT_COUNT_TOPIC = "effectlogtopic";
    public final static String EFFECT_COUNT_GROUP = "effectloggroup";
}
