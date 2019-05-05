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
    public Elevator[] elevators = new Elevator[6];
    public Slider[] elevatorSliders = new Slider[6];
    private void initInsideFloorButton() {
        //目的是从1到20，把0空出来

        for(int i = 1; i <= 10; i++) {
            insideFloorButtons[i] = (Button)root.lookup("#floor"+i);
            if(insideFloorButtons[i] != null) {
                final int iFinal = i;
                insideFloorButtons[i].setOnAction((event) -> {
                    insideFloorButtons[iFinal].setStyle("-fx-background-color:#5264AE;");
                    String elevatorChoiceValue = (String)elevatorChoice.getValue();
                    int elevatorIndex = elevatorChoiceValue.charAt(8)-'0';
                    if(elevators[elevatorIndex].status == Elevator.PAUSE || elevators[elevatorIndex].status == Elevator.UP) {
                        elevators[elevatorIndex].upQueue.add(iFinal);
                    }
                });
            }

        }
    }
    public void initialize() {
        elevatorChoice.setValue("elevator1");
        initInsideFloorButton();
        for(int i = 1; i <= 5; i++) {
            elevatorSliders[i] = (Slider)root.lookup("#slider"+i);
            elevatorSliders[i].setMin(1);
            elevatorSliders[i].setMax(20);

        }
        for(int i = 1; i <= 5;i++){
            elevators[i] = new Elevator(i, this);
            new Thread(elevators[i]).start();
        }
    }

}
