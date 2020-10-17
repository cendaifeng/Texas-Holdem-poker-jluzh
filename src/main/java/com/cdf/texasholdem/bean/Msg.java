package com.cdf.texasholdem.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用的返回 json 的类
 */
public class Msg {

    // 牌桌已存在
    private static final int a = 301;

    // 状态码
    private int code;
    // 提示信息
    private String msg;
    // 用户要返回给浏览器的数据
    private Map<String, Object> extend = new HashMap<>();

    public static Msg success() {
        Msg result = new Msg();
        result.setCode(100);
        result.setMsg("处理成功");
        return result;
    }

    public static Msg fail() {
        Msg result = new Msg();
        result.setCode(200);
        result.setMsg("处理失败");
        return result;
    }

    public Msg() {
    }

    public int getCode() {
        return code;
    }

    public Msg setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    private void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getExtend() {
        return extend;
    }

    private void setExtend(Map<String, Object> extend) {
        this.extend = extend;
    }

    public Msg add(String key, Object value) {
        this.getExtend().put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", extend=" + extend +
                '}';
    }
}
