package io.union.log.common.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HanyuPinyinHelper {
    final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 将文字转为汉语拼音
     *
     * @param ChineseLanguage 要转成拼音的中文
     */
    public String toHanyuPinyin(String ChineseLanguage) {
        char[] cl_chars = ChineseLanguage.trim().toCharArray();
        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 输出拼音全部小写
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        for (int i = 0; i < cl_chars.length; i++) {
            try {
                char ch = cl_chars[i];
                if (String.valueOf(ch).matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音
                    hanyupinyin += PinyinHelper.toHanyuPinyinStringArray(ch, defaultFormat)[0];
                } else {// 如果字符不是中文,则不转换
                    if (Character.isLetterOrDigit(ch)) {
                        hanyupinyin += ch;
                    }
                }
            } catch (Exception e) {
                logger.error(ChineseLanguage + "第" + i + "个字符不能转成汉语拼音");
            }
        }
        return hanyupinyin;
    }
}