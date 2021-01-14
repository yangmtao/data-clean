package xyz.ymtao.config;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.ymtao.util.R;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    //指定出现什么异常执行这方法
    @ExceptionHandler(Exception.class)
    @ResponseBody//为了返回数据
    public R error(Exception e){
        e.printStackTrace();
        return R.error("未知异常");
    }

    //自定义异常执行这方法
    @ExceptionHandler(ZtbException.class)
    @ResponseBody//为了返回数据
    public R ZtbExc(ZtbException e){
//        e.printStackTrace();
        return R.error(e.getCode(),e.getMsg());
    }
}
