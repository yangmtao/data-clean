package xyz.ymtao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2020/12/21  11:15
 */
@Controller
public class PageController {

    @RequestMapping("/upload")
    public String upload(){
        return "upload";
    }
}
