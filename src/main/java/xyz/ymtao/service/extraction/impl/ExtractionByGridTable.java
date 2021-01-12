package xyz.ymtao.service.extraction.impl;

import org.jsoup.select.Elements;
import xyz.ymtao.entity.ZtbDocument;
import xyz.ymtao.service.extraction.Extraction;

import java.math.BigDecimal;

/**
 * @Author: ymt
 * @Description: 解析包含class为gridtable的文档
 * @CreateDate: 2020/12/30  15:59
 */
public class ExtractionByGridTable implements Extraction {
    @Override
    public ZtbDocument doExtraction(Elements elements, String targetEnt) {
        String res = "暂无法识别";
        String type = "暂未分类";
        String keyContent = "";
        // 默认识别率
        double accuracyRate = 0.8;
        if(elements != null && elements.size()>0){
            //存放解析后的金额
            BigDecimal price = new BigDecimal(0);
            // 用于判断是否为目标企业
            boolean isHit = false;
            Elements trList = elements.get(0).getElementsByTag("tr");
            if(trList.size() > 1){
                // 处理表头，找出企业名称，中标金额所在的下标，以及金额单位
                Elements thList = trList.get(0).getElementsByTag("th");
                int entNameIndex,priceIndex;
                entNameIndex=priceIndex=-1;
                boolean isWan = false;
                for(int i=0;i<thList.size();i++){
                    String thText = thList.get(i).text().trim();
                    if("中标供应商".equals(thText)){
                        entNameIndex = i;
                        continue;
                    }
                    if(thText.contains("中标金额")){
                        priceIndex = i;
                        if(thText.contains("万")){
                            isWan = true;
                        }
                    }
                }
                if(entNameIndex == -1 || priceIndex == -1){
                    return Extraction.handleResult(targetEnt, null, null, null, keyContent, type, accuracyRate, res);
                }

                //处理每一条中标信息info
                for(int i=1;i<trList.size();i++){
                    Elements info = trList.get(i).getElementsByTag("td");
                    if(targetEnt.equals(info.get(entNameIndex).text().trim())){
                        isHit =true;
                        String priceStr = info.get(priceIndex).text();
                        price = Extraction.handlePrice(priceStr,price,isWan);
                    }
                }
            }
            if(isHit){
                res = price.toString();
                type = "gridTable";
                accuracyRate = 1;
                keyContent = elements.toString();
            } else {
                res = "未中标";
            }

            return Extraction.handleResult(targetEnt, null, null, null, keyContent, type, accuracyRate, res);
        } else {
            return Extraction.handleResult(targetEnt, null, null, null, keyContent, type, accuracyRate, res);
        }
    }
}
