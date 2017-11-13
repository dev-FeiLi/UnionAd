package io.union.js.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class IPAreaMap {

    private static Logger logger = LoggerFactory.getLogger(IPAreaMap.class);

    private static Map<String, List<String>> map = new HashMap<>();

    static {
        map.put("北京", new ArrayList<>(Collections.singletonList("北京")));
        map.put("上海", new ArrayList<>(Collections.singletonList("上海")));
        map.put("重庆", new ArrayList<>(Collections.singletonList("重庆")));
        map.put("天津", new ArrayList<>(Collections.singletonList("天津")));
        map.put("香港", new ArrayList<>(Collections.singletonList("香港")));
        map.put("台湾", new ArrayList<>(Collections.singletonList("台湾")));
        map.put("澳门", new ArrayList<>(Collections.singletonList("澳门")));
        map.put("黑龙江", new ArrayList<>(Arrays.asList("哈尔滨,齐齐哈尔,鹤岗,双鸭山,鸡西,大庆,伊春,牡丹江,佳木斯,七台河,黑河,绥化,大兴安岭".split(","))));
        map.put("吉林", new ArrayList<>(Arrays.asList("长春,吉林,四平,辽源,通化,白山,松原,白城,延边".split(","))));
        map.put("辽宁", new ArrayList<>(Arrays.asList("沈阳,大连,鞍山,抚顺,本溪,丹东,锦州,葫芦岛,营口,盘锦,阜新,辽阳,铁岭,朝阳".split(","))));
        map.put("内蒙古", new ArrayList<>(Arrays.asList("呼和浩特,包头,乌海,赤峰,通辽,鄂尔多斯,呼伦贝尔,乌兰察布,锡林郭勒,巴彦淖尔,阿拉善,兴安盟".split(","))));
        map.put("河北", new ArrayList<>(Arrays.asList("石家庄,唐山,秦皇岛,邯郸,邢台,保定,张家口,承德,沧州,廊坊,衡水".split(","))));
        map.put("河南", new ArrayList<>(Arrays.asList("郑州,开封,洛阳,平顶山,焦作,鹤壁,新乡,安阳,濮阳,许昌,漯河,三门峡,南阳,商丘,信阳,周口,驻马店".split(","))));
        map.put("山东", new ArrayList<>(Arrays.asList("济南,青岛,淄博,枣庄,东营,潍坊,烟台,威海,济宁,泰安,日照,莱芜,临沂,德州,聊城,滨州,菏泽".split(","))));
        map.put("山西", new ArrayList<>(Arrays.asList("太原,大同,阳泉,长治,晋城,朔州,晋中,运城,忻州,临汾,吕梁地".split(","))));
        map.put("湖北", new ArrayList<>(Arrays.asList("武汉,黄石,襄樊,十堰,荆州,宜昌,荆门,鄂州,孝感,黄冈,咸宁,随州,恩施".split(","))));
        map.put("湖南", new ArrayList<>(Arrays.asList("长沙,株洲,湘潭,衡阳,邵阳,岳阳,常德,张家界,益阳,郴州,永州,怀化,娄底,湘西".split(","))));
        map.put("广东", new ArrayList<>(Arrays.asList("广州,深圳,珠海,汕头,韶关,河源,梅州,惠州,汕尾,东莞,中山,江门,佛山,阳江,湛江,茂名,肇庆,清远,潮州,揭阳,云浮".split(","))));
        map.put("广西", new ArrayList<>(Arrays.asList("南宁,柳州,桂林,梧州,北海,防城港,钦州,贵港,玉林,百色,贺州,河池,来宾,崇左".split(","))));
        map.put("江西", new ArrayList<>(Arrays.asList("南昌,景德镇,萍乡,九江,新余,鹰潭,赣州,吉安,宜春,抚州,上饶".split(","))));
        map.put("陕西", new ArrayList<>(Arrays.asList("西安,铜川,宝鸡,咸阳,渭南,延安,汉中,榆林,安康,商洛".split(","))));
        map.put("海南", new ArrayList<>(Arrays.asList("海口,三亚".split(","))));
        map.put("云南", new ArrayList<>(Arrays.asList("昆明,曲靖,玉溪,保山,昭通,思茅,临沧,丽江,文山,红河,西双版纳,楚雄,大理,德宏,怒江,迪庆".split(","))));
        map.put("江苏", new ArrayList<>(Arrays.asList("南京,徐州,连云港,淮安,宿迁,盐城,扬州,泰州,南通,镇江,常州,无锡,苏州".split(","))));
        map.put("浙江", new ArrayList<>(Arrays.asList("杭州,宁波,温州,嘉兴,湖州,绍兴,金华,衢州,舟山,台州,丽水".split(","))));
        map.put("安徽", new ArrayList<>(Arrays.asList("合肥,芜湖,蚌埠,淮南,马鞍山,淮北,铜陵,安庆,黄山,滁州,阜阳,宿州,巢湖,六安,亳州,池州,宣城".split(","))));
        map.put("福建", new ArrayList<>(Arrays.asList("福州,厦门,三明,莆田,泉州,漳州,南平,龙岩,宁德".split(","))));
        map.put("四川", new ArrayList<>(Arrays.asList("成都,自贡,攀枝花,泸州,德阳,绵阳,广元,遂宁,内江,乐山,南充,宜宾,广安,达州,眉山,雅安,巴中,资阳,阿坝,甘孜,凉山".split(","))));
        map.put("贵州", new ArrayList<>(Arrays.asList("贵阳,六盘水,遵义,安顺,铜仁地,毕节地,黔西南,黔东南,黔南".split(","))));
        map.put("西藏", new ArrayList<>(Arrays.asList("拉萨,那曲,昌都,山南,日喀则,阿里,林芝".split(","))));
        map.put("甘肃", new ArrayList<>(Arrays.asList("兰州,金昌,白银,天水,嘉峪关,武威,张掖,平凉,酒泉,庆阳,定西,陇南,甘南,临夏".split(","))));
        map.put("青海", new ArrayList<>(Arrays.asList("西宁,海东,海北,黄南,海南,果洛,玉树,海西".split(","))));
        map.put("宁夏", new ArrayList<>(Arrays.asList("银川,石嘴山,吴忠,固原".split(","))));
        map.put("新疆", new ArrayList<>(Arrays.asList("乌鲁木齐,克拉玛依,吐鲁番,哈密,和田,阿克苏,喀什,克孜勒苏,巴音郭楞,昌吉,博尔塔拉,伊犁,塔城,阿勒泰".split(","))));
        map.put("其他", new ArrayList<>(Collections.singletonList("其他")));
    }

    public static String province(String location) {
        String province = "其他";
        try {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                if (location.contains(key)) {
                    province = key;
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("province error: " + e);
        }
        return province;
    }

    public static String city(String province, String location) {
        String city = "其他";
        try {
            List<String> values = map.get(province);
            if (null != values && values.size() > 0) {
                for (String value : values) {
                    if (location.contains(value)) {
                        city = value;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("city error: " + e);
        }
        return city;
    }
}
