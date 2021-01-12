package xyz.ymtao.entity;

import org.springframework.data.annotation.Id;

/**
 * @描述
 * @创建人 wgq
 * @创建时间 2021-01-11
 */
public class User {
    private String userName;
    @Id
    private String phone;

    public User() {
    }

    public User(String userName, String phone) {
        this.userName = userName;
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
