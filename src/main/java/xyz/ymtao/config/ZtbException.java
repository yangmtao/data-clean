package xyz.ymtao.config;



/**
 * @author admin
 */
public class ZtbException extends RuntimeException {
    private Integer code;
    private String msg;

    public ZtbException() {
    }

    public ZtbException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
