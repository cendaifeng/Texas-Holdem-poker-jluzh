package com.cdf.texasholdem.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class NetUtil {

    /**
     * 从Cookies中找到name为key的cookie，并返回其value
     * @param cookies Cookie数组
     * @param key 要找的name
     * @return
     */
    public static String getGivenCookies(Cookie[] cookies, String key) {
        String value = null;
        for (Cookie x : cookies) {
            if (x.getName().equals(key)) {
                value = x.getValue();
                break;
            }
        }
        return value;
    }

    /**
     * 检测端口是否在使用
     * @param port 端口号
     * @return 返回 false 即端口未被占用
     */
    public static boolean isLocalPortUsing(int port) {
        boolean flag = false;
        try {
            Socket socket = new Socket("127.0.0.1", port);
            flag = true;
        } catch (IOException e) {
        }
        return flag;
    }

    /**
     * 判断请求端 Ip 地址
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length() = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress="";
            System.out.println("获取ip时发生错误");
        }

        return ipAddress;
    }
}