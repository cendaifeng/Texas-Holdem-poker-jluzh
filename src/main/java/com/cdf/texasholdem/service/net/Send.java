package com.cdf.texasholdem.service.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/*
 *使用多线程封装 发送端
 * 1.初始化DataOutputString
 * 2.接收消息（getFromConsole()+send()+重写run())
 * 3.释放资源
 */
public class Send implements Runnable {
    private BufferedReader console;
    private Socket socket;
    private DataOutputStream dataOutputStream;

    public Send(Socket socket) {
        this.socket = socket;
        console = new BufferedReader(new InputStreamReader(System.in));
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            closeRes();
            System.out.println("----class SendExc----");
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            String consoleMsg = getFromConsole();
            if (!consoleMsg.equals("")) {
                send(consoleMsg);
            }
        }
    }

    private void send(String msg) {
        //返回消息
        try {
            dataOutputStream.writeUTF(msg);
            dataOutputStream.flush();
        } catch (IOException e) {
            System.out.println("----class SendExc outputExc----");
            closeRes();
        }
    }

    private String getFromConsole() {
        try {
            return console.readLine();
        } catch (IOException e) {
            System.out.println("----getFromConsole() Exc----");
            return "";
        }

    }

    private void closeRes() {
        ReleaseResource.release(dataOutputStream);
        Thread.currentThread().interrupt();
        System.out.println(Thread.currentThread()+"--Exc--");
    }
}
