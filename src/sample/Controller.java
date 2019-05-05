package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    public ChoiceBox floorChoice;
    public ChoiceBox elevatorChoice;
    public AnchorPane root;
    private Button[] insideFloorButton;
    private void initInsideFloorButton() {
        //目的是从1到20，把0空出来
        insideFloorButton = new Button[21];
        for(int i = 1; i <= 10; i++) {
            insideFloorButton[i] = (Button)root.lookup("#floor"+i);
            if(insideFloorButton[i] != null) {
                final int iFinal = i;
                insideFloorButton[i].setOnAction((event) -> {
                    insideFloorButton[iFinal].setText(Integer.toString(iFinal*2));
                });
            }

        }
    }
    public void initialize() {
        initInsideFloorButton();
    }

}
