package gui;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class guiController implements Initializable {

    public Label LP, LT, LV, LU, LH, LS;
    public ComboBox comboBox1, comboBox2, comboBox3;
    public ImageView general;
    public ImageView general1;

    public Label type, labelType;
    public TextField tF1, tF2, tF3;
    public Button findButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tF1.setVisible(false);
        tF2.setVisible(false);
        tF3.setVisible(false);
        general1.setVisible(false);
        general.setVisible(false);
        LP.setVisible(false);
        LT.setVisible(false);
        LU.setVisible(false);
        LH.setVisible(false);
        LS.setVisible(false);
        type.setVisible(false);

    }

    public void find(MouseEvent mouseEvent) {
        if (!isInputsValid()){
            return;
        }




























        if (tF3.isVisible() && tF3.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter the value of " + comboBox3.getValue());
        }

    }



    private boolean isInputsValid() {
        if (tF1.isVisible()) {
            if (tF1.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the value of " + comboBox1.getValue());
                return false;
            }
            else if (Integer.parseInt(tF1.getText()) < 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Negative numbers are not allowed");
                return false;
            }
            else if (Integer.parseInt(tF1.getText()) == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText(comboBox1.getValue() +" cannot be zero");
                return false;
            }
        }
        if (tF2.isVisible()) {
            if (tF2.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the value of " + comboBox2.getValue());
                return false;
            }
            else if (Integer.parseInt(tF2.getText()) < 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Negative numbers are not allowed");
                return false;
            }
            else if (Integer.parseInt(tF2.getText()) == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText(comboBox2.getValue() +" cannot be zero");
                return false;
            }
        }



        return true;
    }
}
