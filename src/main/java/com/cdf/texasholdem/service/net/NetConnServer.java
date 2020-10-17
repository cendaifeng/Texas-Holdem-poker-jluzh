package com.cdf.texasholdem.service.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 网络沟通类
 * 多线程下在本地网络建立一个端口下多名玩家的操作通信
 * 同时开启多个牌桌即使用多个端口
 * serversocket 一张牌桌
 * channel 一个玩家（以 cookies: id 辨识）
 */
public class NetConnServer {

    private ServerSocket serversocket;
    private CopyOnWriteArrayList<Channel> allClient = new CopyOnWriteArrayList();

    /**
     * 初始化牌桌对应serversocket （不具备端口检测）
     * @param port 端口号
     * @throws IOException
     */
    public NetConnServer(int port) throws IOException {
        System.out.println("----SERVER----");
        // 1.创建ServerSocket对象 指定服务器端口
        serversocket = new ServerSocket(port);
        Boolean serverClose = false;
        while (!serverClose) {
            // 2.阻塞式监听客户端连接请求
            // 将调用accept函数获取到的threadSocket传入Channel的构造器中，传入外部类当前实例
            Channel channel = new Channel(serversocket.accept(), this);
            new Thread(channel).start();
            // 将当前channel放入数组列表里，以便将来遍历
            allClient.add(channel);
        }
        serversocket.close();
        return;
    }

    static class Channel implements Runnable {

        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;
        private Socket threadSocket;
        private NetConnServer netConnServer;
        private String uname = "";
        private String upwd = "";
        private String msg = "";
        public Channel(Socket threadSocket, NetConnServer netConnServer) {
            this.threadSocket = threadSocket;
            this.netConnServer = netConnServer;
            try {
                dataInputStream = new DataInputStream(threadSocket.getInputStream());
                dataOutputStream = new DataOutputStream(threadSocket.getOutputStream());
                analyzeLogin();
            } catch (Exception e) {
                System.out.println("----constructExc----");
                //若捕获到异常则关闭该套接字
                closeRes();
            }
            System.out.println("与一个客户端发生连接...");
            this.send("欢迎您的到来！");
            this.sendToOthers("***"+this.uname+"进入聊天室***");
        }

        @Override
        public void run() {
            try {
                // 3.接收请求，返回响应
                while (!Thread.currentThread().isInterrupted()) {
                    String msg = receive();
                    if (!msg.equals("")) {
                        boolean isPrivate = msg.startsWith("@");
                        if (isPrivate) {
                            sendPrivate(msg);
                        } else {
                            sendToOthers(this.uname + ": " + msg);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("----run()Exc---");
                // 4.释放资源
                closeRes();
            } finally {
                System.out.print("用户--"+this.uname+"-- ");
                System.out.println("管道资源已释放");
            }
        }

        private void analyzeLogin() throws Exception {
            //分析输入信息
            String data = dataInputStream.readUTF();
            String[] dataArray = data.split("&");
            for(String info: dataArray) {
                String[] userInfo = info.split("=");
                if (userInfo[0].equals("uname")) {
                    uname = userInfo[1];
                    System.out.println("用户名为--> "+uname);
                } else {
                    System.out.print("密码为--> ");
                    { for (int i = 0; i < userInfo[1].length(); i++) {
                        System.out.print('*'); } }
                    System.out.println();
                    upwd = userInfo[1];
                }
            }
            //验证信息是否正确
            if (uname.equals("cendaifeng")&&upwd.equals("0000")) {
                dataOutputStream.writeUTF("登陆成功，欢迎回来");
            } else {
                dataOutputStream.writeUTF("密码或用户名错误！");
            }
            //登录错误断连模块尚未写入
        }

        private void sendPrivate(String msg) {
            int idx = msg.indexOf(":");
            String targetName = msg.substring(1,idx);
            for (Channel c:netConnServer.allClient) {
                if (c.uname.equals(targetName)) {
                    c.send(this.uname+"悄悄地对您说: "+msg.substring(idx+1));
                }
            }
        }

        private void sendToOthers(String msg) {
            {
                for (Channel c:netConnServer.allClient) {
                    //若用户非信息源用户，则向其发送信息
                    if (c.equals(this)) {
                        continue;
                    }
                    c.send(msg);
                }
            }
        }

        private void send(String msg) {
            //返回消息
            try {
                dataOutputStream.writeUTF(msg);
                dataOutputStream.flush();
            } catch (IOException e) {
                System.out.println("----outputExc----");
                closeRes();
            }
        }

        private String receive() {
            //接收消息
            String msg = "";
            try {
                msg = dataInputStream.readUTF();
            } catch (IOException e) {
                System.out.println("----inputExc----");
                closeRes();
            }
            return msg;
        }

        private void closeRes() {
            ReleaseResource.release(dataInputStream, dataOutputStream, threadSocket);
            netConnServer.allClient.remove(this);
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread()+"--Exc--");
            this.sendToOthers("---"+this.uname+"退出聊天室---");
        }

    }

}
