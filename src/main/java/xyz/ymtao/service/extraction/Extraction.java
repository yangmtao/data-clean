package xyz.ymtao.service.extraction;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xyz.ymtao.entity.ZtbDocument;

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
    public ZtbDocument doExtraction(Elements elements, String targetEnt);

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

    public static String handleTable(Element table, String targetEnt){
        BigDecimal price = new BigDecimal("0");
        if(table == null) {return "非本方法处理";}

        Elements trList = table.getElementsByTag("tr");
        // 获取表格头
        Element header = trList != null && trList.size()>0 ? trList.get(0) : null;
        // 根据表格头判断是否为目标表格类型
        if(header != null){
            Elements headerList = header.getElementsByTag("th").size() > 0 ?  header.getElementsByTag("th") : header.getElementsByTag("td");
            if(headerList.size() < 3 || !headerList.get(1).text().contains("中标供应商")){return "暂无法识别";}
            // 判断表头的金额中是否单位为万元
            String priceHeader = headerList.get(3).text();
            boolean isWan = false;
            if(priceHeader.indexOf("万") >= 0){
                isWan = true;
            }

            boolean isTarget = false;
            // 处理表格中的每一条招投标信息
            for(int i=1;i<trList.size();i++){
                Element element = trList.get(i);
                // 取出每一条招投标信息中的各个字段信息
                Elements td = element.getElementsByTag("td");
                if(td !=null && td.size()>=4){
                    String entName = td.get(1).text();
                    entName = entName.replace('（','(');
                    entName = entName.replace('）',')');
                    entName = entName.replace('；',' ');
                    entName = entName.trim();
                    // 如果为目标企业
                    if(targetEnt.equals(entName)){
                        isTarget =true;
                        String priceStr = td.get(3).text();
                        BigDecimal tempPrice;
                        int index = priceStr.indexOf("万");
                        if(isWan || index>=0){
                            tempPrice = new BigDecimal(getNumberFromString(priceStr)).multiply(new BigDecimal("10000"));
                        } else {
                            tempPrice = new BigDecimal(getNumberFromString(priceStr));
                        }
                        price = price.add(tempPrice);
                        System.out.println(targetEnt+ ":"+priceStr+":"+price);
                    }
                }
            }
            if(!isTarget){
                return "疑似未中标";
            }
            // 保留两位小数返回
            return price.setScale(2,BigDecimal.ROUND_HALF_UP).toString();

        } else {
            return "非本方法处理";
        }

    }

    public static ZtbDocument handleResult(String targetEnt,String title,String date,String contents,String keyContent,String type,double accuracyRate,String res){
        ZtbDocument ztbDocument;
        if(res.contains(".")){
            ztbDocument = new ZtbDocument(targetEnt,title,date,contents,keyContent,"中标",type,accuracyRate,res);
        } else {
            ztbDocument = new ZtbDocument(targetEnt,title,date,contents,keyContent,res,type,accuracyRate,"0.00");
        }
       return ztbDocument;
    }
}
