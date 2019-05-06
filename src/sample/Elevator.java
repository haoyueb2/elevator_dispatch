package sample;


import javafx.application.Platform;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Elevator implements Runnable {

    public int currentFloor = 1;

    public final static int PAUSE = 0;
    public final static int UP = 1;
    public final static int DOWN = 2;
    private int selfElevatorIndex;
    public int status = PAUSE;
    public PriorityQueue<Integer> upQueue ;

    public PriorityQueue<Integer> downQueue ;
    private Controller controller;
    //消除重复，有重复的话很可能导致电梯持续运行到顶或底
    private class MyPriQueue<E> extends PriorityQueue<E> {
        MyPriQueue() {
            super();
        }
        //子类构造函数不继承父类的，只能在构造函数里调用
        MyPriQueue(Comparator<E> cmp) {
            super(cmp);
        }

        @Override
        public boolean add(E e) {
            boolean isAdded = false;
            if (!super.contains(e)) {
                isAdded = super.add(e);
            }
            return isAdded;
        }
    }
    public Elevator(int selfElevatorIndex, Controller controller){
        this.selfElevatorIndex = selfElevatorIndex;
        this.controller = controller;
        Comparator<Integer> cmp;
        cmp = (o1, o2) -> o2 -o1;
        upQueue = new MyPriQueue<>();
        downQueue = new MyPriQueue<>(cmp);
    }
    //消除try和catch带来的冗余
    private void mySleep(int time) {
        try {
            Thread.sleep(time);
        }
        catch(Exception exc){
            System.out.println("sleep exception!!");
        }
    }
    public void openDoor() {
        if(this.status == PAUSE ) {
            mySleep(2000);
        }
    }
    @Override
    public void run() {

        while(true){
            //加上线程才能运行
            System.out.print("");
            while(upQueue.size() > 0) {
                status = UP;
                currentFloor++;
                controller.elevatorSliders[selfElevatorIndex].setValue(currentFloor);
                if(upQueue.peek() == currentFloor) {
                    upQueue.poll();
                    controller.insideFloorButtons[currentFloor].setStyle(null);
                    controller.outsideUpButtons[currentFloor].setStyle(null);
                    controller.outsideDownButtons[currentFloor].setStyle(null);
                    //开门关门2s
                    mySleep(2000);
                }
                mySleep(1000);
            }
            while(downQueue.size() > 0) {
                System.out.print("");
                status = DOWN;
                currentFloor--;
                if(downQueue.peek() == currentFloor) {
                    downQueue.poll();
                    controller.insideFloorButtons[currentFloor].setStyle(null);
                }
                controller.elevatorSliders[selfElevatorIndex].setValue(currentFloor);
                mySleep(1000);
            }
            status = PAUSE;

        }
    }
}
