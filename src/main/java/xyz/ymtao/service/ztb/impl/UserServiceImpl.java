package xyz.ymtao.service.ztb.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import xyz.ymtao.config.ZtbException;
import xyz.ymtao.entity.User;
import xyz.ymtao.service.ztb.UserService;
import xyz.ymtao.util.MD5;
import xyz.ymtao.util.R;

import java.util.List;

/**
 * @描述
 * @创建人 wgq
 * @创建时间
 */
@Service
public class UserServiceImpl implements UserService{
    @Autowired
    MongoTemplate mongoTemplate;

    //注册用户
    @Override
    public void add(User user){
        String password = user.getPassword();
        String pwd = MD5.encrypt(password);
        user.setPassword(pwd);
        try {
            mongoTemplate.insert(user);
        }catch (Exception e){
            throw new ZtbException(5003,"数据异常");
        }
    }

    // 登录
    @Override
    public User login(User user){
        Query query = new Query();
        List<User> users = null;

                query.addCriteria(Criteria.where("phone").is(user.getPhone()));
        try {
            users = mongoTemplate.find(query, User.class);
        }catch (Exception e){
            throw new ZtbException(5003,"数据异常");
        }
        if (users.size()>0){
            User userInfo = users.get(0);
            //校验密码
            if(!MD5.encrypt(user.getPassword()).equals(userInfo.getPassword())) {
                throw new ZtbException(5002,"密码错误");
            }
            return userInfo;
        } else {
            return null;
        }
    }
}
