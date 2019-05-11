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
    //是否正在某层短暂停留，用作能否开关门的判断条件
    public boolean isTmpStay = false;
    public PriorityQueue<Integer> upQueue ;

    public PriorityQueue<Integer> downQueue ;
    private Controller controller;

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
    public static void mySleep(int time) {
        try {
            Thread.sleep(time);
        }
        catch(Exception exc){
            System.out.println("sleep exception!!");

        }
    }
    public void openDoor() {
        //到达指定楼层后令设开门时间,开门前如果电梯所有上升下降任务全部完成了要提前设置电梯状态为PAUSE
        //不然在开门时间内较近的低层请求楼层可能仍然以为电梯的状态是UP而可能舍近求远地调度了其他电梯
        if(upQueue.size() == 0 && downQueue.size() == 0 ) {
            status = PAUSE;
        }
        //其他UI控件都在这个线程就能改变了，只有setText不可以，要让application线程去改变
        Platform.runLater(()->controller.eachFloorDisplay[currentFloor].setText(selfElevatorIndex+"号电梯open"));
        Platform.runLater(()->controller.eachElevatorDisplay[selfElevatorIndex].setText("open"));
        mySleep(1500);
        Platform.runLater(()->controller.eachFloorDisplay[currentFloor].setText("closed"));

        if(status == PAUSE) {
            Platform.runLater(()->controller.eachElevatorDisplay[selfElevatorIndex].setText("  "+currentFloor));
        }
    }
    private void handleOutsideOrderButtonStyle() {
        controller.insideFloorButtons[currentFloor].setStyle(null);
       if(controller.eachFloorTarget[currentFloor].upButtonTarget == selfElevatorIndex) {
           controller.outsideUpButtons[currentFloor].setStyle(null);
           controller.eachFloorTarget[currentFloor].upButtonTarget = 0;
       }
        if(controller.eachFloorTarget[currentFloor].downButtonTarget == selfElevatorIndex) {
            controller.outsideDownButtons[currentFloor].setStyle(null);
            controller.eachFloorTarget[currentFloor].downButtonTarget = 0;
        }
    }
    @Override
    public void run() {

        while(true){
            //加上线程才能运行
            System.out.print("");


            while(upQueue.size() > 0) {
                //要在sleep前先设置电梯的状态，防止某一层连续按了上行和下行按钮后只来了一部电梯
                status = UP;
                ////这个是运动的时间间隔,放在头部，从出发到下一层会有sleep而不是瞬移上去
                //到达指定楼层后由openDoor产生sleep
                mySleep(500);

                //currentFloor递增要放在前边，不然可能导致停止时currentFloor比实际值大一
                //upQueue中出现本层请求的原因是因为外部的up和down的按钮依靠此机制开门
                // 但是电梯内部的按钮是不会把本层请求加入队列的，因为电梯内部本身就有开门按钮
                if(upQueue.peek() != currentFloor) {
                    currentFloor++;
                }
                controller.elevatorSliders[selfElevatorIndex].setValue(currentFloor);
                Platform.runLater(()->controller.eachElevatorDisplay[selfElevatorIndex].setText("↑ "+currentFloor));
                if(upQueue.peek() == currentFloor) {
                    isTmpStay = true;
                    upQueue.poll();
                    handleOutsideOrderButtonStyle();
                    openDoor();
                    isTmpStay = false;
                }
            }
            //细节同Up按钮，不再重复注释
            while(downQueue.size() > 0) {
                status = DOWN;
                mySleep(500);

                if(downQueue.peek() != currentFloor) {
                    currentFloor--;
                }
                controller.elevatorSliders[selfElevatorIndex].setValue(currentFloor);
                Platform.runLater(()->controller.eachElevatorDisplay[selfElevatorIndex].setText("↓ "+currentFloor));
                if(downQueue.peek() == currentFloor) {
                    isTmpStay = true;
                    downQueue.poll();
                    handleOutsideOrderButtonStyle();
                    openDoor();
                    isTmpStay = false;
                }
            }
            status = PAUSE;

        }
    }
}
