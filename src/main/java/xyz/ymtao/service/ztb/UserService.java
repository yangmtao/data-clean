package xyz.ymtao.service.ztb;

import org.springframework.context.annotation.Bean;
import xyz.ymtao.entity.User;

/**
 * @描述
 * @创建人 wgq
 * @创建时间
 */
public interface UserService {
    void add(User user);

    User login(User user);
}
