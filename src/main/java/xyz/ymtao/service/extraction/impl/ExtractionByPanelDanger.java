package xyz.ymtao.service.extraction.impl;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xyz.ymtao.entity.ZtbDocument;
import xyz.ymtao.service.extraction.Extraction;

import java.math.BigDecimal;

/**
 * @Author: ymt
 * @Description: 解析包含class为panel-danger的文档
 * @CreateDate: 2020/12/30  15:38
 */
public class ExtractionByPanelDanger implements Extraction {
    @Override
    public ZtbDocument doExtraction(Elements elements, String targetEnt) {
        String res = "暂无法识别";
        String type = "暂未分类";
        String keyContent = "";
        // 默认识别率
        double accuracyRate = 0.8;
        // 如果有目标信息块
        if(elements != null && elements.size()>0){
            // 用于存放抽取出的金额数据
            BigDecimal price = new BigDecimal("0.00");
            // 对每个分包进行处理
            for(int j=0; j<elements.size();j++){
                Element element = elements.get(j);
                Elements tdList = element.getElementsByTag("td");
                // 如果分包存在内容且为目标公司
                if(tdList.size()>2 && targetEnt.equals(tdList.get(2).text())){
                    // 格式化价格
                    String[] priceList = tdList.get(1).text().split(",");
                    int index = priceList[0].indexOf("￥");
                    if(index>=0){
                        priceList[0]=priceList[0].substring(index+1);
                    }
                    StringBuilder tempPrice = new StringBuilder();
                    for(int x=0;x<priceList.length;x++){
                        tempPrice.append(priceList[x]);
                    }
                    // 将价格转为BigDecimal类型
                    BigDecimal decimalPrice = new BigDecimal(tempPrice.toString());
                    // 由于可能多个分包都是目标公司的，因此相加
                    price = price.add(decimalPrice);
                }
            }
            res =  price.toString();
            keyContent = elements.toString();
            type = "panelDanger";
        }
            return Extraction.handleResult(targetEnt, null, null, null, keyContent, type, accuracyRate, res);
    }
}
