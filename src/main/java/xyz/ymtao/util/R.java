package xyz.ymtao.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2020/12/25  9:25
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /**
     * 1 成功
     * 2 token失效
     * 3 xid失效
     * 4 参数异常
     */

    public R() {
        put("code", StatusCodeEnum.SUCCESS.getValue());
        put("msg", "success");
        put("data", "");
    }

    public static R error() {
        return error(StatusCodeEnum.OPERATE_ERROR);
    }

    public static R error(String msg) {
        return error(StatusCodeEnum.OPERATE_ERROR.getValue(), msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R error(StatusCodeEnum obj){
        R r = new R();
        r.put("code", obj.getValue());
        r.put("msg", obj.getText());
        return r;
    }


    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 校验工具
     * @param r
     * @return
     */
    public static Boolean validate(R r){
        if(r != null && "1".equals(r.get("code").toString())){
            return true;
        }
        return false;
    }

}
