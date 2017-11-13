package io.union.js.common.redis;

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
    ADS_SETTING_CLOSE_IMAGE("close_image_url"), // 关闭按钮的图片地址
    ADS_SETTING_AD_ICON("ad_icon_url"), // “广告”按钮的图片地址
    ADS_SETTING_CLICK_DOMAIN("click_domain"), // 点击跳转的域名
    ADS_SETTING_CLICK_URL("click_uri"), // 点击跳转的地址
    ADS_SETTING_COUNT_DOMAIN("pv_count_domain"), // 计算有效数的域名
    ADS_SETTING_COUNT_URL("pv_count_uri"), // 计算有效数的地址
    ADS_SETTING_JS_UR("js_server_domain"), // 系统JS的服务器地址
    ADS_SETTING_CNZZ("cnzz_code"), // cnzz统计代码
    ADS_SETTING_IMG_URL("img_server_domain"), // 系统图片资源的服务器地址

    ADS_CHEAT_IPPV("%s:ipmatchpv:date:%s"), // 每天一个IP对应多少个pv
    ADS_CHEAT_IPUV("%s:ip:%s:matchuv:date:%s"), //每天一个IP对应多少个uv
    ADS_CHEAT_IPURL("%s:ip:%s:matchurl:data:%s"), //每天一个IP对应多少个域名
    ADS_CHEAT_UVPV("%s:uvmatchpv:date:%s"), // 每天一个UV对应多少个pv
    ADS_CHEAT_UVIP("%s:uv:%s:matchip:date:%s"), //每天一个UV对应多少个ip
    ADS_CHEAT_UVURL("%s:uv:%s:matchurl:data:%s"), //每天一个UV对应多少个域名
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
