package xyz.ymtao.service.extraction;

import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2020/12/30  15:32
 */
public interface Extraction {
    /**
     * @param elements 欲解析的HTML doc文档
     * @param targetEnt 目标企业
     * @return
     */
    public String doExtraction(Elements elements, String targetEnt);

    /**
     * @param str 从字符串中抽取数字和小数点
     * @return
     */
    public static String getNumberFromString(String str){
        if(str == null || "".equals(str)){
            return "";
        }
        String regEx = "[^\\.0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static BigDecimal handlePrice(String priceStr,BigDecimal price,boolean isWan){
        if(priceStr.contains("万") || isWan){
            price = price.add(new BigDecimal(Extraction.getNumberFromString(priceStr)).multiply(new BigDecimal("10000")));
        } else {
            price = price.add(new BigDecimal(Extraction.getNumberFromString(priceStr)));
        }
        return price.setScale(2,BigDecimal.ROUND_HALF_UP);
    }
}
