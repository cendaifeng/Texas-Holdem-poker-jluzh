package com.cdf.texasholdem.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用的返回 json 的类
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
// 保证序列化值为null的时候，key不序列化
public class ServerResponse implements Serializable {
    // 状态码
    private int code;
    // 提示信息
    private String msg;
    // 用户要返回给浏览器的数据
    private Map<String, Object> data = new HashMap<>();

    public static ServerResponse success() {
        ServerResponse result = new ServerResponse();
        result.setCode(100);
        result.setMsg("处理成功");
        return result;
    }

    public static ServerResponse fail() {
        ServerResponse result = new ServerResponse();
        result.setCode(200);
        result.setMsg("处理失败");
        return result;
    }

    public ServerResponse() {
    }

    public int getCode() {
        return code;
    }

    public ServerResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    private void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    private void setData(Map<String, Object> data) {
        this.data = data;
    }

    public ServerResponse add(String key, Object value) {
        this.getData().put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", extend=" + data +
                '}';
    }
}
