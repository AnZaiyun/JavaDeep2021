package com.anzaiyun.lock;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {
    private ArrayList<Integer> arrayList = new ArrayList<Integer>();

    public static void main(String[] args) {
        ReentrantLockTest lockTest = new ReentrantLockTest();

        new Thread(){
            @Override
            public void run(){
                lockTest.insert(Thread.currentThread());
            }
        }.start();

        new Thread(){
            @Override
            public void run(){
                lockTest.insert(Thread.currentThread());
            }
        }.start();

    }

    private void insert(Thread currentThread) {
        Lock lock  = new ReentrantLock();
        lock.lock();
        try{
            System.out.println(currentThread.getName()+ " 获得了锁");
            for(int i=0; i<5; i++) {
                arrayList.add(i);
            }
        }catch (Exception e){

        }finally {
            lock.unlock();
            System.out.println(currentThread.getName()+ " 释放了锁");
        }

    }
}
