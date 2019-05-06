package sample;


import javafx.application.Platform;

import java.util.PriorityQueue;

public class Elevator implements Runnable {

    public int currentFloor = 1;

    public final static int PAUSE = 0;
    public final static int UP = 1;
    public final static int DOWN = 2;
    private int selfElevatorIndex;
    public int status = PAUSE;
    public PriorityQueue<Integer> upQueue = new PriorityQueue<>();
    public PriorityQueue<Integer> downQueue = new PriorityQueue<>();
    private Controller controller;
    public Elevator(int selfElevatorIndex, Controller controller){
        this.selfElevatorIndex = selfElevatorIndex;
        this.controller = controller;
    }
    @Override
    public void run() {

        while(true){
            System.out.println("run!"+ selfElevatorIndex);
            while(upQueue.size() > 0) {
                status = UP;
                currentFloor++;
                if(upQueue.peek() == currentFloor) {
                    upQueue.poll();
                    //System.out.println("arrive!"+ currentFloor);
                    controller.insideFloorButtons[currentFloor].setStyle(null);
                    //Platform.runLater(()->controller.insideFloorButtons[currentFloor].setStyle(null));
                }

                System.out.println("run!"+ selfElevatorIndex+"floor:"+selfElevatorIndex);
                controller.elevatorSliders[selfElevatorIndex].setValue(currentFloor);
                //Platform.runLater(()-> controller.elevatorSliders[selfElevatorIndex].setValue(currentFloor));
                try {
                    Thread.sleep(1000);
                }
                catch(Exception exc){
                    System.out.println("sleep exception!!");
                }
            }
            while(downQueue.size() > 0) {
                status = DOWN;
                if(downQueue.peek() == currentFloor) {
                    downQueue.poll();
                }
                currentFloor--;
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
