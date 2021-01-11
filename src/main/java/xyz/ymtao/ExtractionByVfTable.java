package xyz.ymtao;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xyz.ymtao.service.extraction.Extraction;

import java.math.BigDecimal;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2021/1/6  14:17
 */
public class ExtractionByVfTable implements Extraction {

    @Override
    public String doExtraction(Elements elements, String targetEnt) {
        if(elements != null && elements.size()>0){
            Element table = elements.get(0);
            if(table == null) {return "非本方法处理";}
            BigDecimal price = new BigDecimal("0");
            // 获取表格每一行内容
            Elements trList = table.getElementsByTag("tr");
            // 获取表格头
            Element header = trList != null && trList.size()>0 ? trList.get(0) : null;
            // 根据表格头判断是否为目标表格类型
            if(header != null){
                // 获取表格头内容
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
                        // 过滤中英文
                        entName = entName.replace('（','(').replace('）',')').replace('；',' ').trim();
                        // 如果为目标企业
                        if(targetEnt.equals(entName)){
                            isTarget =true;
                            String priceStr = td.get(3).text();
                            BigDecimal tempPrice;
                            int index = priceStr.indexOf("万");
                            if(isWan || index>=0){
                                tempPrice = new BigDecimal(Extraction.getNumberFromString(priceStr)).multiply(new BigDecimal("10000"));
                            } else {
                                tempPrice = new BigDecimal(Extraction.getNumberFromString(priceStr));
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
        return "暂无法识别";
    }
}
