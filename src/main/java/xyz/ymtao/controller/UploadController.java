package xyz.ymtao.controller;

import io.swagger.annotations.ApiParam;
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
@RestController
public class UploadController {

    @Autowired
    MongoTemplate mongoTemplate;

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam @ApiParam(value = "文件") MultipartFile file) throws IOException {
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
                line = line.replaceAll(arr[0],"<div style=\"background: yellow;\">" + arr[0] + "</div>");
                ZtbDocument document = new ZtbDocument(arr[0],arr[1],arr[2],line);
                mongoTemplate.insert(document);
            }
        }
        in.close();
        reader.close();
        br.close();
        return "ok";
    }
}
