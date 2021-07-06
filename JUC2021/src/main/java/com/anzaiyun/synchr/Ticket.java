package com.anzaiyun.synchr;

public class Ticket {
    private int num = 30;

    public synchronized void sale() throws InterruptedException {
        this.num--;
        System.out.println("卖出后，当前票数"+num);
        //这里模拟阻塞
        this.wait(5000);
    }

    public synchronized void buy() throws InterruptedException {
        this.num++;
        System.out.println("买入后，当前票数"+this.num);
        //这里模拟阻塞
        this.wait(5000);
    }

    public void getLast(){
        System.out.println("当前剩余票数。。。。");
    }

    public static void main(String[] args) {
        Ticket ticket = new Ticket();
        try {
            //阻塞后，必须等待sale方法执行完毕，buy方法才会执行，sale和buy操作是互斥的，所以这样没关系，但是对于getLast来说并不是，所以
            //getLast方法不应该被阻塞住
            ticket.sale();
            ticket.getLast();
            ticket.buy();
            ticket.getLast();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
