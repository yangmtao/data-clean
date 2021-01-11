package xyz.ymtao.service.extraction.impl;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xyz.ymtao.service.extraction.Extraction;

import java.math.BigDecimal;

/**
 * @Author: ymt
 * @Description: 解析包含class为panel-heading的文档
 * @CreateDate: 2020/12/30  16:45
 */
public class ExtractionByPanelHeading implements Extraction {
    @Override
    public String doExtraction(Elements elements, String targetEnt) {
        // 如果有目标信息块
        if(elements != null && elements.size()>0){
            // 用于存放抽取出的金额数据
            BigDecimal price = new BigDecimal("0.00");
            // 对每个分包进行处理
            for(int i=0; i<elements.size();i++){
               Elements pList = elements.get(i).getElementsByTag("p");
               for(int j =0;j<pList.size();j++){
                   String pText = pList.get(j).toString();
                   String[] arr = pText.split("<br>");
                   if(arr.length >2){
                       if(targetEnt.equals(arr[0].split("：")[1].trim())){
                           String priceStr = arr[1].split("：")[1];
                           price = Extraction.handlePrice(priceStr,price,false);
                       } else {
                           return "疑似未中标";
                       }
                   }
               }
            }
            return price.toString();
        } else {
            return "暂无法识别";
        }
    }
}
