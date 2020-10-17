package com.cdf.texasholdem.controller;

import java.util.concurrent.FutureTask;

public class FutureTaskTest {

        public static int begin() {

            final FutureTask<Integer> futureTask = new FutureTask(() -> {
                FutureTaskTest.run();
                return 1;
            });

            new Thread(futureTask).start();
            return 2;
        }

        public static void run() {
            System.out.println("睡眠中");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public static void main(final String[] args) {
            int i = FutureTaskTest.begin();
            System.out.println(i);
        }

}
