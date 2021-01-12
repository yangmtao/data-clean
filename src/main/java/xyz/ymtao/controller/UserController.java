package xyz.ymtao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import xyz.ymtao.entity.User;
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

    @PostMapping("/register")
    public String regUser(@RequestBody User user){
        try {
            mongoTemplate.insert(user);
        }catch (Exception e){
            return "error";
        }
        return "ok";
    }
    @GetMapping("/login")
    public R loginUser(@RequestParam String phone){
        Query query = new Query();
        if(phone == null || phone == ""){
            return R.error();
        }
        List<User> users = null;
        query.addCriteria(Criteria.where("phone").is(phone));
        try {
            users = mongoTemplate.find(query, User.class);
        }catch (Exception e){
            return R.error();
        }
        return R.ok().put("data",users);
    }
}
