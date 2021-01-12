package xyz.ymtao.service.extraction.impl;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xyz.ymtao.entity.ZtbDocument;
import xyz.ymtao.service.extraction.Extraction;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2021/1/11  14:04
 */
public class ExtractionByVtTable implements Extraction {
    @Override
    public ZtbDocument doExtraction(Elements elements, String targetEnt) {
        // 获取所有招投标信息
        Elements tableList = elements.size() >0 ? elements.get(0).getElementsByTag("table") : new Elements();
        Element table = tableList.size() > 0 ? tableList.get(0) : null;
        String keyContent  = table != null ? table.toString() : "";
        String res="暂无法识别";
        String type = "暂未分类";
        // 默认识别率
        double accuracyRate = 0.8;
        res = Extraction.handleTable(table, targetEnt);
        if("非本方法处理".equals(res)){
            res = "暂无法识别";
        }
        if(!"暂无法识别".equals(res)){
            type = "VfTable";
            accuracyRate = 1;
        }
        final ZtbDocument ztbDocument = Extraction.handleResult(targetEnt, null, null, null, keyContent, type, accuracyRate, res);
        return ztbDocument;
    }
}
