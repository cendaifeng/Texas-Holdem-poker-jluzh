package com.cdf.texasholdem.service.net;

import java.io.*;
import java.net.Socket;

/**
 * 网络客户类
 * 作为一名玩家在一个端口下的操作通信入口
 * 该类只用于玩家做以下操作：
 * 下注；
 * 弃牌；
 */
public class NetClient {

    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    /**
     * 当一名玩家加入牌桌，该构造器会调用
     * @param port 端口
     * @param index 第几号位
     * @param username 玩家昵称
     * @throws IOException
     */
    public NetClient(int port, int index, String username) throws IOException {
        System.out.println("----CLIENT " + username + " ----");
        // 1.创建Socket对象 指定要连接的服务器地址和端口 发送连接请求
        socket = new Socket("localhost", port);
        // 2.发送服务请求，接收响应
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        new Thread(new Send(this.socket)).start();
        new Thread(new Receive(this.socket)).start();
//        // 3.释放资源
//        closeRes();
    }

    private void closeRes() {
        ReleaseResource.release(socket);
    }

}
