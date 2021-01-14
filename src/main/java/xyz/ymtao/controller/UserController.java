package xyz.ymtao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import xyz.ymtao.entity.User;
import xyz.ymtao.service.ztb.UserService;
import xyz.ymtao.util.R;

import java.util.List;
import java.util.Map;

/**
 * @描述
 * @创建人 wgq
 * @创建时间 2021-01-11
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public R regUser(@RequestBody User user){
        if(user.getPhone() == null || user.getPhone() == ""){
            return R.error("电话号码为空！");
        }
        if(user.getPassword() == null || user.getPassword() == ""){
            return R.error("密码为空！");
        }
        userService.add(user);
        return R.ok();
    }
    @PostMapping("/login")
    public R loginUser(@RequestBody User user){

        if(user.getPhone() == null || user.getPhone() == ""){
            return R.error("电话号码为空！");
        }
        if(user.getPassword() == null || user.getPassword() == ""){
            return R.error("密码为空！");
        }

        User userInfo = userService.login(user);
        return R.ok().put("data",userInfo);
    }
}
