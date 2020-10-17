package com.cdf.texasholdem.controller;

import com.cdf.texasholdem.bean.Person;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockingQueueTest {

    private ArrayBlockingQueue<Person> queue = new ArrayBlockingQueue<Person>(8);

    private ArrayList<Person> players;

    @Test
    public void main() {
        CopyOnWriteArrayList<Person> players = new CopyOnWriteArrayList<>(Arrays.asList(new Person[]{
                new Person("Jackey"),
                new Person("Joe"),
                new Person("Nana"),
                new Person("CEN"),
                new Person("BAIYUAN"),
                new Person("LINGDAO"),
                new Person("TAOGE"),
                new Person("Berlin")}));

        this.queue.addAll(players);
        try {
            System.out.println(queue.take());
            System.out.println(queue.take());
            System.out.println(queue.take());
            System.out.println(queue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
