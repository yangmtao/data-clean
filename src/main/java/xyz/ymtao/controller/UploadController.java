package xyz.ymtao.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.ymtao.entity.ZtbDocument;

import java.io.*;
import java.util.Arrays;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2020/12/21  11:36
 */
@Api(tags = "招投标原始信息上传")
@RestController
public class UploadController {

    @Autowired
    MongoTemplate mongoTemplate;

    @ApiOperation(value = "上传招投标信息文件")
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam @ApiParam(value = "文件") MultipartFile file, @RequestParam(name = "phone") String phone) throws IOException {
        String oringinalFileName = file.getOriginalFilename();
        int index = oringinalFileName.lastIndexOf(".");
        int len = oringinalFileName.length();
        String fileType = oringinalFileName.substring(index+1, len);
        String[] fileTypes = {"log", "txt", "html"};
        if(fileType == null || fileType == "" || !Arrays.asList(fileTypes).contains(fileType.toLowerCase())){
            return "上传文件格式错误";
        }
        int maxSize = 1048576 * 30;
        if(file.getSize() > maxSize){
            return "文件大小不能超过30MB";
        }

        InputStream in = file.getInputStream();
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(reader);
        String  line = null;
        while((line=br.readLine()) != null){
            String[] arr = line.split("#");
            if(arr != null && arr.length > 3){
                String content = arr[0] + arr[1] +arr[2];
                line = line.substring(content.length() +3);
                line = isFooter(line);
                line = line.replaceAll(arr[0],"<div style=\"background:#bfbfbf;max-width:400px;font-weight:600;\">" + arr[0] + "</div>");
                ZtbDocument document = new ZtbDocument(arr[0],arr[1],arr[2],line,phone);
                try{
                    mongoTemplate.insert(document);
                } catch (Exception e){
                    System.out.println("插入数据重复：" + arr[1]);
                }
            }
        }
        in.close();
        reader.close();
        br.close();
        return "ok";
    }

    //去除特定样式的标签
    public static String isFooter ( String line){
        Document doc= Jsoup.parse(line, "UTF-8");
        Elements select = doc.select(".footer").remove();
        return doc.toString();
    }
}
