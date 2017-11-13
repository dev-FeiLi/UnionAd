package io.union.log.common.redis;

/**
 * Created by Administrator on 2017/7/11.
 */
public enum RedisKeys {

    ADS_AD_MAP("adsadmap"),//所有的广告
    ADS_TYPE_SIZE_AD_MAP("adsad:type:%s:%s:%s"),// 类型+尺寸所对应的广告id

    ADS_ZONE_MAP("adszonemap"),//所有的广告位
    ADS_USER_MAP("adsusermap"),//所有的注册用户

    ADS_PLAN_MAP("adsplanmap"),//所有的计划

    ADS_SITE_MAP("adssitemap"),//所有网站
    ADS_UID_SITE_MAP("adssite:uid:%s"),//站长名下的网站，filed=host，value=siteid

    ADS_POP_IP_MAP("adspopmap:%s:%s"),//针对站长/广告/计划设置中，每个IP弹一次，filed=ip，value=0
    ADS_DIV_IP_MAP("adsdivmap:%s:%s"),//针对站长/广告/计划设置中，每个IP加暗层，filed=ip，value=0

    ADS_SETTING_MAP("adssettingmap"),// 系统全局设置
    ADS_SETTING_LOG_VISIT("log_visit_save"), // 是否把PV存入数据库
    ADS_SETTING_LOG_CLICK("log_click_save"), // 是否把点击存入数据库

    LOCK_TO_DEL("allzoopath"), //每天zookeeper产生的路径，需要筛选删除
    STAT_HOUR_REQS_DATE_MAP("stathourreqs:date:%s"), // 每个zoneid每天每个小时的请求量对应的key
    STAT_HOUR_IP_DATE_MAP("stathourip:uid:%s:date:%s"), // 每个uid每个小时的ip量
    STAT_HOUR_PV_DATE_MAP("stathourpv:uid:%s:date:%s"), // 每个uid每个小时的pv量
    STAT_HOUR_UV_DATE_MAP("stathouruv:uid:%s:date:%s"), // 每个uid每个小时的uv量
    STAT_HOUR_CLICK_DATE_MAP("stathourclick:uid:%s:date:%s"), // 每个uid每个小时的点击量
    STAT_HOUR_CLICKIP_DATE_MAP("stathourclickip:uid:%s:date:%s"), // 每个uid每个小时的点击ip量
    STAT_HOUR_CLICKUV_DATE_MAP("stathourclickuv:uid:%s:date:%s"), // 每个uid每个小时的点击uv量
    STAT_HOUR_MONEY_DATE_MAP("stathourmoney:uid:%s:date:%s"), // 每个uid每个小时的结算额
    UNION_REQUESTS_DATE_MAP("unionrequest:date:%s"), // 每个zoneid每天请求总量对应的key
    UNION_STATS_DATE_MAP("unionstats:uid:%s:zoneid:%s:date:%s"), // 每个uid的每个zoneid每天的广告收入
    LOG_BROWSER_MAP("browser:name:%s:os:%s:date:%s"), // 每天browser的版本数量信息
    LOG_CITY_MAP("logcity:%s:date:%s"), // 每天浏览者地区的数量信息
    LOG_OS_MAP("logos:%s:date:%s"), // 每天浏览器系统的数量信息
    LOG_SCREEN_MAP("screen:%s:date:%s"), // 每天屏幕尺寸的数量信息

    LIMIT_MONEY_PLAN_DATE_MAP("planlimitmoney:date:%s"), // 每个计划每天广告主的支出
    TEMP_UV_DATE_MAP("tempuv:plan:%s:date:%s"), // 每天每个计划浏览者的uv
    TEMP_CUV_DATE_MAP("tempcuv:plan:%s:date:%s"), // 每天每个计划点击者的uv
    TEMP_IP_DATE_MAP("tempip:plan:%s:date:%s"), // 每天每个计划浏览者的ip
    TEMP_CIP_DATE_MAP("tempcip:plan:%s:date:%s"), // 每天每个计划点击者的ip
    ;

    private String property;

    RedisKeys(String propery) {
        this.property = propery;
    }

    @Override
    public String toString() {
        return property;
    }
}
