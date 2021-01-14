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
    private String password;

    public User() {
    }

    public User(String userName, String phone, String password) {
        this.userName = userName;
        this.phone = phone;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
