package xyz.ymtao.service.extraction.impl;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import xyz.ymtao.entity.ZtbDocument;
import xyz.ymtao.service.extraction.Extraction;

import java.math.BigDecimal;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2021/1/6  14:17
 */
public class ExtractionByVfTable implements Extraction {

    @Override
    public ZtbDocument doExtraction(Elements elements, String targetEnt) {
        Elements tableList = elements.get(0).getElementsByTag("table");
        String res = "暂无法识别";
        String type = "暂未分类";
        String keyContent = "";
        // 默认识别率
        double accuracyRate = 0.8;
        if (tableList != null && tableList.size() > 0) {
            Element table = tableList.get(0);
            keyContent = table != null ? table.toString() : "";
            res = Extraction.handleTable(table, targetEnt);
            if (!"暂无法识别".equals(res)) {
                type = "VfTable";
                accuracyRate = 1;
            }
            if("非本方法处理".equals(res)){
                res = "暂无法识别";
            }
        }
        final ZtbDocument ztbDocument = Extraction.handleResult(targetEnt, null, null, null, keyContent, type, accuracyRate, res);
        return ztbDocument;
    }
}
