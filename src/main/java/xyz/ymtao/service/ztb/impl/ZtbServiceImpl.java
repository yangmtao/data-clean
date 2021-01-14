package xyz.ymtao.service.ztb.impl;

import com.mongodb.client.result.DeleteResult;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import xyz.ymtao.entity.ZtbDocument;
import xyz.ymtao.entity.ZtbSummary;
import xyz.ymtao.service.ztb.ZtbService;
import xyz.ymtao.util.Const;
import xyz.ymtao.util.R;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2021/1/11  14:41
 */
@Service
public class ZtbServiceImpl implements ZtbService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public long updateZtb(ZtbDocument ztbDocument) {
        Query query = new Query();
        query.addCriteria(Criteria.where("title").is(ztbDocument.getTitle()));
        Update update = new Update();
        update.set("amount",ztbDocument.getAmount())
                .set("type",ztbDocument.getType())
                .set("keyContent",ztbDocument.getKeyContent())
                .set("accuracyRate",ztbDocument.getAmount());

        return mongoTemplate.updateFirst(query,update,ZtbDocument.class).getModifiedCount();
    }

    @Override
    public <E> void exportToExcel(List<E> ztbList, String userId,HttpServletResponse response) {
        if(ztbList != null && ztbList.size() >= 1){
             this.exportToExcelByEntName(ztbList,response,ZtbDocument.class);
        } else {
            List<ZtbSummary> summaryList = mongoTemplate.find(new Query().addCriteria(Criteria.where("phone").is(userId)),ZtbSummary.class);
             this.exportToExcelByEntName(summaryList,response,ZtbSummary.class);

        }

    }

    @Override
    public long removeData(ZtbDocument ztbDocument) {
        Class<? extends ZtbDocument> aClass = ztbDocument.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        Query query =new Query();
        for(int i=0;i<declaredFields.length;i++){
            declaredFields[i].setAccessible(true);
            try{
                Object o = declaredFields[i].get(ztbDocument);
                if(o != null){
                    query.addCriteria(Criteria.where(declaredFields[i].getName()).is(o));
                }
            } catch (Exception e){
                System.out.println("构造query失败，"  + declaredFields[i].getName());
                return 0;
            }

        }
        DeleteResult remove = mongoTemplate.remove(query, ZtbDocument.class);
        System.out.println("用户" + ztbDocument.getPhone() + "删除数据：" + query);
        return remove.getDeletedCount();
    }

    private <E> void exportToExcelByEntName(List<E> list, HttpServletResponse response, Class tClass){
        List<ZtbSummary> summaryList = new ArrayList<>();
        List<ZtbDocument> ztbList = new ArrayList<>();
        String[] headerItems = {};
        String className = tClass.getSimpleName();
        if("ZtbSummary".equals(className)){
            headerItems = Const.SUMMARYEXCELITEMS;
            Class<ZtbSummary> tSummary = ZtbSummary.class;
            for(int i =0;i<list.size();i++){
                summaryList.add(tSummary.cast(list.get(i)));
            }
        } else if("ZtbDocument".equals(className)){
            headerItems = Const.EXCELITEMS;
            Class<ZtbDocument> tZtb = ZtbDocument.class;
            for(int i =0;i<list.size();i++){
                ztbList.add(tZtb.cast(list.get(i)));
            }
        }
        // 创建excel导出工作对象
        HSSFWorkbook wb = new HSSFWorkbook();
        // 创建excel工作表
        HSSFSheet sheet = wb.createSheet("中标信息汇总");
        // 创建表头
        HSSFRow header = sheet.createRow(0);
        // 设置表头样式
        HSSFCellStyle headerStyle = wb.createCellStyle();
        //背景色
        headerStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        //水平对齐方式
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        //垂直对齐方式
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置字体
        Font font=wb.createFont();
        font.setFontName("微软雅黑");
        font.setBold(true);
        headerStyle.setFont(font);
        // 应用样式
        header.setRowStyle(headerStyle);
        //设置表头单元格名称
        int rowNo=0;

        for(int i = 0; i< headerItems.length; i++){
            HSSFCell cell=header.createCell(rowNo++);
            cell.setCellValue(headerItems[i]);
            cell.setCellStyle(headerStyle);
        }

        // 写入内容
        for(int i=0;i<list.size();i++){
            //创建行
            HSSFRow row = sheet.createRow(i+1);
            System.out.println("第"+(i+1)+"条数据："+ (ztbList.size() > 0 ? ztbList.get(i).getTitle() : summaryList.get(i).getEntName()));
            //写入每一行内容
            int k=0;
            row.createCell(k++).setCellValue(ztbList.size() > 0 ? ztbList.get(i).getEntName() : summaryList.get(i).getEntName());
            row.createCell(k++).setCellValue(ztbList.size() > 0 ? ztbList.get(i).getTitle() : String.valueOf(summaryList.get(i).getHitNum()));
            row.createCell(k++).setCellValue(ztbList.size() > 0 ? ztbList.get(i).getAmount() : summaryList.get(i).getAmount());
            if(ztbList.size() > 0){
                row.createCell(k++).setCellValue(ztbList.get(i).getDate());
                row.createCell(k).setCellValue(ztbList.get(i).getStatus());
            }
        }

        // 将写好的excel导出到客户端
        String entName = ztbList.size() > 0 ? ztbList.get(0).getEntName() : "全部";
        entName += "中标信息汇总";
        ServletOutputStream out;
        try{
            response.setContentType("application/ms-excel;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition","attachment;filename="+new String(entName.getBytes("gb2312"), "ISO-8859-1")+".xls");
            out = response.getOutputStream();
            wb.write(out);
            out.flush();
            out.close();
        } catch (Exception e){
            System.out.println("导出失败！");
        }
    }

    public static void main(String[] args) {
        ZtbService ztbService = new ZtbServiceImpl();
        ZtbDocument ztbDocument = new ZtbDocument();
        ztbDocument.setEntName("重庆华亚家私有限公司");
        ztbDocument.setPhone("13883300323");
        ztbService.removeData(ztbDocument);
    }
}
