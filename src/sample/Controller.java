package sample;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;

public class Controller {
    public ChoiceBox floorChoice;
    public ChoiceBox elevatorChoice;
    public AnchorPane root;
    public Button[] insideFloorButtons = new Button[21];
    public Button[] outsideUpButtons = new Button[6];
    public Button[] outsideDownButtons = new Button[6];
    public Elevator[] elevators = new Elevator[6];
    public Slider[] elevatorSliders = new Slider[6];
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
        for(int i =1;i <= 5; i++) {
            outsideUpButtons[i] = (Button)root.lookup("#up"+i);
            if(outsideUpButtons[i] != null) {
                outsideUpButtons[i].setOnAction((event) -> {
                    handleOutsideUpOrder();
                });
            }
        }

    }
    private void handleOutsideUpOrder() {
        int outsideCurFloor;
        String floorValue = (String)floorChoice.getValue();
        outsideCurFloor =Integer.parseInt(floorValue.substring(5));
        for(int i = 1; i <= 5; i ++ ) {
            outsideUpButtons[i].setStyle("-fx-background-color:#5264AE;");
        }
        int nearestElevatorIndex = 0;
        int minDistance = 100;//先设一个很大的数
        //先在up里边找，是如果Up和Pause的电梯离楼层距离相等时，优先调度Up状态的
        for(int i = 1; i <= 5; i++) {
            //由于只要有待处理的上升楼层电梯就是上升状态，所以楼层相等时Up的楼层也得开
            if(elevators[i].status == Elevator.UP && elevators[i].currentFloor<= outsideCurFloor) {
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
                //电梯开门
            }
        }

    }
    private void handleInsideOrder(int chosenFloor) {
        String elevatorChoiceValue = (String)elevatorChoice.getValue();
        int elevatorIndex = elevatorChoiceValue.charAt(8)-'0';
        //请求不是本层才点亮按钮
        if(elevators[elevatorIndex].currentFloor != chosenFloor) {
            insideFloorButtons[chosenFloor].setStyle("-fx-background-color:#5264AE;");
        }
        if(chosenFloor > elevators[elevatorIndex].currentFloor) {

            elevators[elevatorIndex].upQueue.add(chosenFloor);
        }
        if(chosenFloor < elevators[elevatorIndex].currentFloor) {
            elevators[elevatorIndex].downQueue.add(chosenFloor);
        }
    }
    public void initialize() {
        elevatorChoice.setValue("elevator1");
        floorChoice.setValue("floor1");
        initInsideFloorButtons();
        initOutsideOrderButtons();
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
