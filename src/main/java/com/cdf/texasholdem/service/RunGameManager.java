package com.cdf.texasholdem.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class RunGameManager {

    public static ConcurrentHashMap<Integer, RunGame> runGameHashMap = new ConcurrentHashMap<>();

    private static final ReentrantLock reentrantLock = new ReentrantLock();

    public static ConcurrentHashMap<Integer, RunGame> getRunGameHashMap() {
        return runGameHashMap;
    }

    /**
     * 创建牌桌 (同步)
     * 查看当前桌号是否存在，若已存在则创建失败
     * @param index 桌号
     * @param table RunGame示例
     * @return 是否成功
     */
    public static boolean addRunGame(Integer index, RunGame table) {
        if ( !runGameHashMap.containsKey(index) ){
            runGameHashMap.put(index, table);
        } else {
            return false;
        }
        return true;
    }

}
