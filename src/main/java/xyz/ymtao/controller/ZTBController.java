package xyz.ymtao.controller;

import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;
import xyz.ymtao.entity.ZtbSummary;
import xyz.ymtao.service.extraction.impl.ExtractionByVfTable;
import xyz.ymtao.entity.TemplateType;
import xyz.ymtao.entity.ZtbDocument;
import xyz.ymtao.service.extraction.Extraction;
import xyz.ymtao.service.extraction.impl.ExtractionByPanelDanger;
import xyz.ymtao.service.extraction.impl.ExtractionByGridTable;
import xyz.ymtao.service.extraction.impl.ExtractionByPanelHeading;
import xyz.ymtao.service.extraction.impl.ExtractionByVtTable;
import xyz.ymtao.service.ztb.ZtbService;
import xyz.ymtao.util.R;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2020/12/21  17:04
 */
@Api(tags = "招投标信息相关接口")
@RestController
@CrossOrigin
public class ZTBController {

    private static final String COLLECTION_NAME = "ztbDocument";

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ZtbService ztbService;

    private Extraction extraction;

    private static final String[] types= {"content","price","entName"};

    @ApiOperation(value = "招投标信息查询")
    @GetMapping("/filter")
    public R doFilter(@RequestParam(name = "phone") String phone, @RequestParam(name = "entName",required = false) @ApiParam(value = "企业名称") String entName,
                      @RequestParam(required = false) @ApiParam(value = "模板类型") String type,
                      @RequestParam(required = false)  @ApiParam(value = "中标起始日期") String startTime,
                      @RequestParam(required = false) @ApiParam(value = "中标公告标题") String title){
        Query query = new Query();
        query.addCriteria(Criteria.where("phone").is(phone));
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
            for(int i=0; i<list.size() && list.get(i).getAmount()==null;i++){
                // 将每条招投标信息文档化
                Document doc = Jsoup.parse(list.get(i).getContent(), "UTF-8");
                // 存放解析结果
                ZtbDocument res;

                Elements elements;
                // 尝试根据不同class获取目标信息块
                if((elements = doc.getElementsByClass("panel-danger")).size() > 0){
                   extraction = new ExtractionByPanelDanger();
                } else if((elements = doc.getElementsByClass("gridtable")).size() > 0){
                   extraction = new ExtractionByGridTable();
                } else if((elements = doc.getElementsByClass("panel-heading")).size() > 0){
                    extraction = new ExtractionByPanelHeading();
                } else if((elements=doc.getElementsByClass("vT_detail_content")).size()>0){
                    extraction = new ExtractionByVtTable();
                } else if ((elements=doc.getElementsByClass("vF_detail_content")).size()>0){
                    extraction = new ExtractionByVfTable();
                }

                // 将解析结果更新
                if(extraction != null){
                    res = extraction.doExtraction(elements, list.get(i).getEntName());
                    list.get(i).setAmount(res.getAmount());
                    list.get(i).setAccuracyRate(res.getAccuracyRate());
                    list.get(i).setType(res.getType());
                    list.get(i).setStatus(res.getStatus());
                    list.get(i).setKeyContent(res.getKeyContent());
                    ztbService.updateZtb(list.get(i));
                }

            }
        } else {
            R.error("未查询到相关企业的招投标信息");
        }
        return R.ok().put("data", list);
    }

    @ApiOperation(value = "获取所有模板类型")
    @GetMapping("/template/type")
    public R getTemplateType(){
        List<TemplateType> templateType = mongoTemplate.find(new Query(), TemplateType.class, "templateType");
        return R.ok().put("data",templateType);
    }

    @ApiOperation(value = "保存中标金额")
    @PostMapping("/save/amount")
    public R saveAmount(@RequestBody  ZtbDocument ztbDocument){
        Query query = new Query();
        query.addCriteria(Criteria.where("title").is(ztbDocument.getTitle()));

        // 先更新汇总信息
        ZtbDocument ztbDoc = mongoTemplate.findOne(query, ZtbDocument.class, COLLECTION_NAME);
        Query sumQuery = new Query(Criteria.where("entName").is(ztbDoc.getEntName()));
        ZtbSummary ztbSummary = mongoTemplate.findOne(sumQuery,ZtbSummary.class,"ztbSummary");
        // 如果该企业的中标信息还未进行过汇总
        if(ztbSummary == null){
            ztbSummary = new ZtbSummary();
            ztbSummary.setAmount(ztbDocument.getAmount());
            ztbSummary.setEntName(ztbDoc.getEntName());
            ztbSummary.setHitNum(1);
            mongoTemplate.insert(ztbSummary);
        } else {
            BigDecimal total = new BigDecimal(ztbSummary.getAmount());
            Update summaryUpdate = new Update();
            // 如果该条中标信息进行过汇总
            if(ztbDoc.getSummaryed()){
                BigDecimal oldAmount = new BigDecimal(ztbDoc.getSummaryAmount());
                total = total.subtract(oldAmount);
            } else {
                // 该条中标信息未进行过汇总,中标次数加1
                Integer hitNum = ztbSummary.getHitNum();
                hitNum++;
                summaryUpdate.set("hitNum",hitNum);
            }
            total = total.add(new BigDecimal(ztbDocument.getAmount()));
            summaryUpdate.set("amount",total.toString());
            UpdateResult updateRes = mongoTemplate.updateFirst(sumQuery, summaryUpdate, ZtbSummary.class);
            if(updateRes.getModifiedCount() <1){
                return R.error("中标金额保存失败！");
            }
        }

        // 再更新该条中标信息的金额
        Update update = new Update();
        update.set("amount",ztbDocument.getAmount());
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ZtbDocument.class);
        if(updateResult.getModifiedCount() <1){
            return R.error("中标金额保存失败！");
        }

        return R.ok("中标金额保存成功");

    }

    // 将中标信息导出为excel
    @ApiOperation(value = "中标信息导出为excel")
    @GetMapping("/export/excel")
    public void exportToExcel(@RequestParam(name ="entName",required = false) @ApiParam(value = "企业名称，不传时为导出所有汇总信息") String entName,
                           @RequestParam(name ="userId",required = true) @ApiParam(value = "用户ID") String userId,
                           HttpServletResponse response) throws IOException {
        Query query = new Query();
        query.addCriteria(Criteria.where("phone").is(userId));
        List<ZtbDocument> ztbList = null;
        if(entName!=null){
            query.addCriteria(Criteria.where("entName").is(entName));
            ztbList = mongoTemplate.find(query,ZtbDocument.class,COLLECTION_NAME);
            if(ztbList.size() < 1){
                response.getWriter().println("暂无可导出的中标信息！");
            }
        }
        ztbService.exportToExcel(ztbList,userId,response);
    }
}
