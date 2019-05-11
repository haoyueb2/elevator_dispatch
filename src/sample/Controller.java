package sample;

import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;



public class Controller {
    //设置为public是因为elevator要在运行中更新UI组件状态
    public AnchorPane root;
    public Button[] insideFloorButtons = new Button[21];
    public Button[] outsideUpButtons = new Button[21];
    public Button[] outsideDownButtons = new Button[21];
    public Elevator[] elevators = new Elevator[6];
    public Slider[] elevatorSliders = new Slider[6];
    public Label[] eachFloorDisplay = new Label[21];
    public ToggleGroup  chooseEles = new ToggleGroup();
    public RadioButton[] allChooseEle = new RadioButton[6];
    public Label[] eachElevatorDisplay = new Label[6];
    public Button openButton = new Button();
    public Button alert = new Button();
    public Label alertDisplay = new Label();
    public class targetElevator {
        public int upButtonTarget = 0;
        public int downButtonTarget = 0;
    }
    public targetElevator[] eachFloorTarget= new targetElevator[21];
    private void initInsideFloorButtons() {
        //目的是从1到20，把0空出来
        for(int i = 1; i <= 20; i++) {
            insideFloorButtons[i] = (Button)root.lookup("#floor"+i);
            if(insideFloorButtons[i] != null) {
                final int chosenFloor = i;
                insideFloorButtons[i].setOnAction((event) -> {
                    handleInsideOrder(chosenFloor);
                });
            }
        }
    }
    private void initOutsideOrderButtons() {
        for(int i =1;i <= 20; i++) {
            //上行按钮
            outsideUpButtons[i] = (Button)root.lookup("#up"+i);
            if(outsideUpButtons[i] != null) {
                final int outsideCurFloor = i;
                outsideUpButtons[i].setOnAction((event) -> {
                    handleOutsideUpOrder(outsideCurFloor);
                });
            }
            //下行按钮
            outsideDownButtons[i] = (Button)root.lookup("#down"+i);
            if(outsideDownButtons[i] != null) {
                final int outsideCurFloor = i;
                outsideDownButtons[i].setOnAction((event) -> {
                    handleOutsideDownOrder(outsideCurFloor);
                });
            }
        }
    }
    private void handleOutsideUpOrder(int outsideCurFloor) {
        //因为本层不加入队列，应该加上所有电梯都不在这层时才设置style,不然最后无法恢复style。
        boolean isExist = false;
        for(int i = 1; i <= 5; i++) {
            if(elevators[i].currentFloor == outsideCurFloor) {
               isExist = true;
            }
        }
        if(!isExist) {
            outsideUpButtons[outsideCurFloor].setStyle("-fx-background-color:#B0E0E6;");
        }
        int nearestElevatorIndex = 0;
        int minDistance = 100;//先设一个很大的数
        //下降按钮已经调度了的电梯不考虑
        int busyElevator = eachFloorTarget[outsideCurFloor].downButtonTarget;
        //先在up里边找，是如果Up和Pause的电梯离楼层距离相等时，优先调度Up状态的
        for(int i = 1; i <= 5; i++) {
            //由于只要有待处理的上升楼层电梯就是上升状态，所以楼层相等时Up的楼层也得开
            if(i!= busyElevator && elevators[i].status == Elevator.UP && elevators[i].currentFloor<= outsideCurFloor) {
                int distance = outsideCurFloor - elevators[i].currentFloor;
                if(distance < minDistance) {
                    minDistance = distance;
                    nearestElevatorIndex = i;
                }
            }
        }
        for(int i = 1;i <= 5; i++) {
            if(elevators[i].status == Elevator.PAUSE) {
                int distance = Math.abs(outsideCurFloor-elevators[i].currentFloor);
                if(distance < minDistance) {
                    minDistance = distance;
                    nearestElevatorIndex = i;
                }
            }
        }
        if(nearestElevatorIndex != 0) {
            //此时电梯来自上方下方都有可能
            if(outsideCurFloor - elevators[nearestElevatorIndex].currentFloor > 0) {
                elevators[nearestElevatorIndex].upQueue.add(outsideCurFloor);
            } else if(outsideCurFloor - elevators[nearestElevatorIndex].currentFloor < 0){
                elevators[nearestElevatorIndex].downQueue.add(outsideCurFloor);
            } else if(outsideCurFloor - elevators[nearestElevatorIndex].currentFloor == 0) {
                //本层楼与电梯所处楼层相同，此时up按钮起一个开门的作用，一直按的话电梯也会停在本层
                //up按钮此时只可能调度到UP和PAUSE状态的电梯，UP状态的电梯要加入UpQueue才可以
                //否则加入downQueue而UP状态的电梯还有任务就无法实现开门，PAUSE状态的电梯都行所以此时选择加入upQueue
                elevators[nearestElevatorIndex].upQueue.add(outsideCurFloor);
            }
            eachFloorTarget[outsideCurFloor].upButtonTarget = nearestElevatorIndex;
        }
        //如果现在没有找到合理的，当前时间点难以衡量哪部电梯最优，等会儿再找
        else {
            Elevator.mySleep(1000);
            handleOutsideUpOrder(outsideCurFloor);
        }

    }
    private void handleOutsideDownOrder(int outsideCurFloor) {
        //因为本层不加入队列，应该加上所有电梯都不在这层时才设置style,不然最后无法恢复style。
        boolean isExist = false;
        for(int i = 1; i <= 5; i++) {
            if(elevators[i].currentFloor == outsideCurFloor) {
                isExist = true;
            }
        }
        if(!isExist) {
            outsideDownButtons[outsideCurFloor].setStyle("-fx-background-color:#B0E0E6;");
        }
        int nearestElevatorIndex = 0;
        int minDistance = 100;//先设一个很大的数
        //上行按钮已经调度的电梯不考虑
        int busyElevator = eachFloorTarget[outsideCurFloor].upButtonTarget;
        //先在Down里边找，是如果Down和Pause的电梯离楼层距离相等时，优先调度Down状态的

        for(int i = 1; i <= 5; i++) {
            //由于只要有待处理的下降电梯就是下降状态，所以楼层相等时Down的楼层也得开
            if(i!= busyElevator && elevators[i].status == Elevator.DOWN && elevators[i].currentFloor>= outsideCurFloor) {
                int distance = elevators[i].currentFloor - outsideCurFloor;
                if(distance < minDistance) {
                    minDistance = distance;
                    nearestElevatorIndex = i;
                }
            }
        }
        for(int i = 1;i <= 5; i++) {
            if(elevators[i].status == Elevator.PAUSE) {
                int distance = Math.abs(outsideCurFloor-elevators[i].currentFloor);
                if(distance < minDistance) {
                    minDistance = distance;
                    nearestElevatorIndex = i;
                }
            }
        }
        if(nearestElevatorIndex != 0) {
            //此时电梯来自上方下方都有可能
            if(outsideCurFloor - elevators[nearestElevatorIndex].currentFloor > 0) {
                elevators[nearestElevatorIndex].upQueue.add(outsideCurFloor);
            } else if(outsideCurFloor - elevators[nearestElevatorIndex].currentFloor < 0){
                elevators[nearestElevatorIndex].downQueue.add(outsideCurFloor);
            } else if(outsideCurFloor - elevators[nearestElevatorIndex].currentFloor == 0) {
                //理由同Up按钮
                elevators[nearestElevatorIndex].downQueue.add(outsideCurFloor);
            }
            eachFloorTarget[outsideCurFloor].downButtonTarget = nearestElevatorIndex;
        }
        //如果现在没有找到合理的，当前时间点难以衡量哪部电梯最优，等会儿再找
        else {
            Elevator.mySleep(1000);
            handleOutsideDownOrder(outsideCurFloor);
        }

    }

    private void handleInsideOrder(int chosenFloor) {
        int elevatorIndex =0;
        for(int i = 1; i <= 5; i++) {
            if(allChooseEle[i].isSelected() == true) {
                elevatorIndex = i;
            }
        }
        //请求不是本层才点亮按钮
        if(elevators[elevatorIndex].currentFloor != chosenFloor) {
            insideFloorButtons[chosenFloor].setStyle("-fx-background-color:#B0E0E6;");
        }
        if(chosenFloor > elevators[elevatorIndex].currentFloor) {

            elevators[elevatorIndex].upQueue.add(chosenFloor);
        }
        if(chosenFloor < elevators[elevatorIndex].currentFloor) {
            elevators[elevatorIndex].downQueue.add(chosenFloor);
        }
    }
    private void initOpenButton () {
        openButton = (Button)root.lookup("#open");

        //将本层楼加入电梯的任务队列以实现开门效果
        openButton.setOnAction((event -> {
            int elevatorIndex =0;
            for(int i = 1; i <= 5; i++) {
                if(allChooseEle[i].isSelected() == true) {
                    elevatorIndex = i;
                }
            }
            //只有当电梯到达某层暂时停留时或者在某一层长期处于PAUSE状态时才可以开关门。电梯运行状态不允许开关门
            if(elevators[elevatorIndex].isTmpStay == true || elevators[elevatorIndex].status == Elevator.PAUSE) {
                if (elevators[elevatorIndex].status == Elevator.UP || elevators[elevatorIndex].status == Elevator.PAUSE) {
                    elevators[elevatorIndex].upQueue.add(elevators[elevatorIndex].currentFloor);
                } else if (elevators[elevatorIndex].status == Elevator.DOWN) {
                    elevators[elevatorIndex].downQueue.add(elevators[elevatorIndex].currentFloor);
                }
            }
        }));
    }
    private void initAlertButton() {
        alert = (Button)root.lookup("#alert");
        alertDisplay = (Label)root.lookup("#alertDisplay");
        alert.setOnAction((event)->{
            int elevatorIndex =0;
            for(int i = 1; i <= 5; i++) {
                if(allChooseEle[i].isSelected() == true) {
                    elevatorIndex = i;
                }
            }
            if(alertDisplay.getText().length() >= 40) {
                alertDisplay.setText("");
            }
            alertDisplay.setText(alertDisplay.getText()+"\n"+elevatorIndex+"号电梯发出报警！！");
        });
    }
    public void initialize() {

        for(int i = 1; i <=5;i++) {
            allChooseEle[i]=(RadioButton)root.lookup("#chooseEle"+i);
            allChooseEle[i].setToggleGroup(chooseEles);
        }
        allChooseEle[1].setSelected(true);
        for(int i = 1; i <= 5;i++) {
            eachElevatorDisplay[i] = (Label)root.lookup("#eleDisplay" +i);
        }
        initInsideFloorButtons();
        initOutsideOrderButtons();
        initOpenButton();
        initAlertButton();
        for(int i = 1; i <= 20; i++) {
            eachFloorTarget[i] = new targetElevator();
        }
        for(int i = 1; i <= 20; i++) {
            eachFloorDisplay[i]= (Label)root.lookup("#display"+i);
            eachFloorDisplay[i].setText("closed");
        }
        for(int i = 1; i <= 5; i++) {
            elevatorSliders[i] = (Slider)root.lookup("#slider"+i);
            elevatorSliders[i].setMin(1);
            elevatorSliders[i].setMax(20);
            elevatorSliders[i].setShowTickLabels(true);
            elevatorSliders[i].setShowTickMarks(true);
            elevatorSliders[i].setMinorTickCount(0);
            elevatorSliders[i].setMajorTickUnit(1);

        }
        for(int i = 1; i <= 5;i++){
            elevators[i] = new Elevator(i, this);
            new Thread(elevators[i]).start();
        }
    }

}
