package xyz.ymtao.util;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2020/12/25  9:25
 */
public enum StatusCodeEnum {
    PARAM_INVALID(-1,"参数无效"),

    FAIL(0,"操作失败"),

    SUCCESS(1,"操作成功"),

    TOKEN_INVALID(2,"无效的token"),

    USERINFO_NULL(4,"用户信息不存在"),

    PASSWORD_ERROR(5,"密码输入有误"),

    NOT_LOGGED_IN(7,"还未登录,请先登录"),

    TOKEN_EXPIRE(8,"用户登录已超时"),

    ERROR_CODE(9,"错误的验证码"),

    REPEAT_REGIST_PHONE(10,"手机号已经被注册使用"),

    PHONE_NOT_EXIST(11,"手机号为空"),

    CODE_INVALID(12,"验证码失效"),

    CODE_ERROR(13,"验证码错误"),

    CODE_SENDED(14,"短信已发送,请等待"),

    OPERATE_WAIT(15,"操作进行中，请等待"),

    REQ_SUCCESS_DATA(81,"接口查询成功,且返回了数据"),

    REQ_SUCCESS_NO_DATA(82,"接口查询成功,未返回数据"),

    REQ_ERROR(83,"接口查询报错"),

    REQ_FAIL(84,"接口查询失败"),

    NOTEXISTS(99,"数据不存在"),

    FUZZY_QUERY_OVER(201,"您是非企业认证用户，当前的免费次数已使用完！"),

    USER_STOP_USE(202,"当前用户已被停用！"),

    USER_CANCELLATION(203,"当前用户已注销"),

    OPERATE_ERROR(500,"操作失败");


    private int value;

    private String text;

    StatusCodeEnum(int value,String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }



    public String getText() {
        return text;
    }
}
