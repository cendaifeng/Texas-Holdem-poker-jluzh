package com.cdf.texasholdem.service.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/*
 *使用多线程封装 接收端
 * 1.初始化DataInputString
 * 2.接收消息（receive()+重写run())
 * 3.释放资源
 */
public class Receive implements Runnable {
    private DataInputStream dataInputStream;
    private Socket socket;

    public Receive(Socket socket) {
        this.socket = socket;
        try {
            this.dataInputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeRes();
            System.out.println("----class ReceiveExc----");
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            String msg = receive();
            if (!msg.equals("")) {
                System.out.println(msg);
            }
        }
    }

    private String receive() {
        //接收消息
        String msg = "";
        try {
            msg = dataInputStream.readUTF();
        } catch (IOException e) {
            System.out.println("----class ReceiveExc InputExc----");
            closeRes();
            e.printStackTrace();
        }
        return msg;
    }

    private void closeRes() {
        ReleaseResource.release(dataInputStream);
        Thread.currentThread().interrupt();
        System.out.println(Thread.currentThread()+"--Exc--");
    }
}
