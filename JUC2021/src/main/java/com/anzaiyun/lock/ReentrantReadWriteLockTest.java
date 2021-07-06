package com.anzaiyun.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockTest {
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static void main(String[] args) {
        final ReentrantReadWriteLockTest lockTest = new ReentrantReadWriteLockTest();

        new Thread(){
            @Override
            public void run(){
                lockTest.justRead(Thread.currentThread());
            }
        }.start();

        new Thread(){
            @Override
            public void run(){
                lockTest.justWrite(Thread.currentThread());
            }
        }.start();

        new Thread(){
            @Override
            public void run(){
                lockTest.justRead(Thread.currentThread());
            }
        }.start();
    }

    private void justWrite(Thread currentThread) {
        readWriteLock.writeLock().lock();
        try {
            long start = System.currentTimeMillis();
            System.out.println(currentThread.getName()+"正在进行写操作");
            Thread.sleep(2000);
            System.out.println(currentThread.getName()+"写操作完毕");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void justRead(Thread currentThread) {
        readWriteLock.readLock().lock();
        try{
            long start = System.currentTimeMillis();
            while(System.currentTimeMillis() - start <= 1) {
                System.out.println(currentThread.getName()+"正在进行读操作");
            }
            System.out.println(currentThread.getName()+"读操作完毕");

        }catch (Exception e){

        }finally {
            readWriteLock.readLock().unlock();
        }
    }
}
