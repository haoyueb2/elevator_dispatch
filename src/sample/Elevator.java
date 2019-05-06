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
    @Override
    public void run() {

        while(true){
            System.out.println("detectUp!elevator:"+ selfElevatorIndex);
            while(upQueue.size() > 0) {
                status = UP;
                currentFloor++;
                if(upQueue.peek() == currentFloor) {
                    upQueue.poll();
                    controller.insideFloorButtons[currentFloor].setStyle(null);
                }
                controller.elevatorSliders[selfElevatorIndex].setValue(currentFloor);
                try {
                    Thread.sleep(1000);
                }
                catch(Exception exc){
                    System.out.println("sleep exception!!");
                }
            }
            while(downQueue.size() > 0) {
                System.out.println("detectDown!elevator:"+ selfElevatorIndex);
                status = DOWN;
                currentFloor--;
                if(downQueue.peek() == currentFloor) {
                    downQueue.poll();
                    controller.insideFloorButtons[currentFloor].setStyle(null);
                }
                controller.elevatorSliders[selfElevatorIndex].setValue(currentFloor);
                try {
                    Thread.sleep(1000);
                }
                catch(Exception exc){
                    System.out.println("sleep exception!!");
                }
            }
            status = PAUSE;

        }
    }
}
