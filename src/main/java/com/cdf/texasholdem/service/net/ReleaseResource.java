package com.cdf.texasholdem.service.net;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/*
 * 方法类：专门用于释放资源
 */
public class ReleaseResource {
    public static void release(Closeable ... targets) {
        for (Closeable target:targets) {
            try {
                if (target != null)
                    target.close();
            } catch (IOException e) {
                System.out.println("----releaseExc----");
            }

        }

    }
}
