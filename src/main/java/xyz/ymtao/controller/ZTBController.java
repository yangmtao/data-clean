package xyz.ymtao.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import xyz.ymtao.entity.TemplateType;
import xyz.ymtao.entity.ZtbDocument;
import xyz.ymtao.service.extraction.Extraction;
import xyz.ymtao.service.extraction.impl.ExtractionByPanelDanger;
import xyz.ymtao.service.extraction.impl.ExtractionByGridTable;
import xyz.ymtao.service.extraction.impl.ExtractionByPanelHeading;
import xyz.ymtao.util.R;

import java.util.List;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2020/12/21  17:04
 */
@RestController
@CrossOrigin
public class ZTBController {

    private static final String COLLECTION_NAME = "ztbDocument";

    @Autowired
    private MongoTemplate mongoTemplate;

    private Extraction extraction;

    private static final String[] types= {"content","price","entName"};

    @GetMapping("/filter")
    public R doFilter(@RequestParam(name = "entName",required = false) String entName,@RequestParam(required = false) String type,
                      @RequestParam(required = false) String startTime, @RequestParam(required = false) String title){
        Query query = new Query();
        if(entName != null && entName != ""){
            query.addCriteria(Criteria.where("entName").is(entName));
        }
        if(title != null && title != ""){
            query.addCriteria(Criteria.where("title").is(title));
        }
        if (type != null && type != "") {
            System.out.println(type);
            query.addCriteria(Criteria.where("type").is(type));
        }
        if (startTime != null && startTime != "") {
            query.addCriteria(Criteria.where("date").gt(startTime));
        }

        // 从mongodb查出目标数据
        List<ZtbDocument> list = mongoTemplate.find(query, ZtbDocument.class,COLLECTION_NAME);
        System.out.println(query.toString() + "===size:" + list.size());
        // 查出存在目标公司的招投标信息
        if(list != null){
//            for(int i=0; i<list.size();i++){
//                // 将每条招投标信息文档化
//                Document doc = Jsoup.parse(list.get(i).getContent(), "UTF-8");
//                // 存放解析结果
//                String res = "暂无法解析";
//
//                Elements elements;
//                // 尝试根据不同class获取目标信息块
//                if((elements = doc.getElementsByClass("panel-danger")).size() > 0){
//                   extraction = new ExtractionByPanelDanger();
//                } else if((elements = doc.getElementsByClass("gridtable")).size() > 0){
//                   extraction = new ExtractionByGridTable();
//                } else if((elements = doc.getElementsByClass("panel-heading")).size() > 0){
//                    extraction = new ExtractionByPanelHeading();
//                }
//                if(extraction != null){
//                    res = extraction.doExtraction(elements, list.get(i).getEntName());
//                }
//
//                list.get(i).setAmount(res);
//            }
        } else {
            R.error("未查询到相关企业的招投标信息");
        }
        return R.ok().put("data", list);
    }

    @GetMapping("/template/type")
    public R getTemplateType(){
        List<TemplateType> templateType = mongoTemplate.find(new Query(), TemplateType.class, "templateType");
        return R.ok().put("data",templateType);
    }
}
