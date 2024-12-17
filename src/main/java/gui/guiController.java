package gui;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import steamTables.Controller;
import steamTables.DataBase;
import steamTables.Steam;

import java.net.URL;
import java.util.ResourceBundle;

public class guiController implements Initializable {

    public Label LP, LT, LV, LU, LH, LS;
    public ComboBox<String> comboBox1, comboBox2, comboBox3;
    public ImageView general;
    public ImageView general1;

    public Label type, labelType;
    public TextField tF1, tF2, tF3;
    public Button findButton;

    private Controller controller;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tF1.setVisible(false);
        tF2.setVisible(false);
        tF3.setVisible(false);
        general1.setVisible(false);
        general.setVisible(false);
        LP.setVisible(false);
        LV.setVisible(false);
        LT.setVisible(false);
        LU.setVisible(false);
        LH.setVisible(false);
        LS.setVisible(false);
        type.setVisible(false);
        labelType.setVisible(false);
        controller = new Controller();
        comboBox1.setItems(FXCollections.observableArrayList("Option A", "Option B", "Option C"));
        comboBox2.setItems(FXCollections.observableArrayList("Option 1", "Option 2", "Option 3"));
        comboBox3.setItems(FXCollections.observableArrayList("Choice X", "Choice Y", "Choice Z"));
        comboBox1.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateComboBoxOptions(comboBox2, newValue);
        });

        comboBox2.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateComboBoxOptions(comboBox1, newValue);
        });

    }

    private void updateComboBoxOptions(ComboBox<String> comboBox, String selectedOption) {
        if ("Option A".equals(selectedOption)) {
            comboBox.setItems(FXCollections.observableArrayList("Option 1", "Option 2", "Option 3"));
        } else if ("Option B".equals(selectedOption)) {
            comboBox.setItems(FXCollections.observableArrayList("Option 4", "Option 5", "Option 6"));
        } else {
            comboBox.setItems(FXCollections.observableArrayList("Option 7", "Option 8", "Option 9"));
        }
    }

    public void find(MouseEvent mouseEvent) {
        if (!isInputsValid()){
            return;
        }
        String chosenQ1 = (String) comboBox1.getSelectionModel().getSelectedItem();
        String chosenQ2 = (String) comboBox2.getSelectionModel().getSelectedItem();
        String q1 = tF1.getText();
        String q2 = tF2.getText();
        double v1 = Double.parseDouble(q1);
        double v2 = Double.parseDouble(q2);
        Steam steam = new Steam();
        if (chosenQ1.equals("Temperature")) {
            if (chosenQ2.equals("Pressure")) {
                steam = controller.findTheSteamUsingTP(v1, v2);
            } else if (chosenQ2.equals("X")) {
                steam = controller.findTheSteamUsingTX(v1, v2);
            } else if (chosenQ2.equals("Volume")) {
                steam = controller.findTheSteamUsingTV(v1, v2);
            } else if (chosenQ2.equals("Enthalpy")) {
                steam = controller.findTheSteamUsingTH(v1, v2);
            } else if (chosenQ2.equals("Entropy")) {
                steam = controller.findTheSteamUsingTS(v1, v2);
            }
        }
        if (chosenQ1.equals("Pressure")) {
            if (chosenQ2.equals("Temperature")) {
                steam = controller.findTheSteamUsingTP(v2, v1); // Reversed values
            } else if (chosenQ2.equals("X")) {
                steam = controller.findTheSteamUsingPX(v1, v2);
            } else if (chosenQ2.equals("Volume")) {
                steam = controller.findTheSteamUsingPV(v1, v2);
            } else if (chosenQ2.equals("Enthalpy")) {
                steam = controller.findTheSteamUsingPH(v1, v2);
            } else if (chosenQ2.equals("Entropy")) {
                steam = controller.findTheSteamUsingPS(v1, v2);
            }
        }
        if (chosenQ1.equals("Entropy")) {
            if (chosenQ2.equals("Temperature")) {
                steam = controller.findTheSteamUsingTS(v2, v1);
            } else if (chosenQ2.equals("Pressure")) {
                steam = controller.findTheSteamUsingPS(v2, v1);
            } else if (chosenQ2.equals("X")) {
                steam = controller.findTheSteamUsingXS(v2, v1);
            }
        }
        if (chosenQ1.equals("Enthalpy")) {
            if (chosenQ2.equals("Temperature")) {
                steam = controller.findTheSteamUsingTH(v2, v1);
            } else if (chosenQ2.equals("Pressure")) {
                steam = controller.findTheSteamUsingPH(v2, v1);
            } else if (chosenQ2.equals("X")) {
                steam = controller.findTheSteamUsingHX(v1, v2);
            }
        }
        if (chosenQ1.equals("X")) {
            if (chosenQ2.equals("Temperature")) {
                steam = controller.findTheSteamUsingTX(v2, v1);
            } else if (chosenQ2.equals("Pressure")) {
                steam = controller.findTheSteamUsingPX(v2, v1);
            } else if (chosenQ2.equals("Volume")) {
                steam = controller.findTheSteamUsingVX(v2, v1);
            } else if (chosenQ2.equals("Enthalpy")) {
                steam = controller.findTheSteamUsingHX(v2, v1);
            } else if (chosenQ2.equals("Entropy")) {
                steam = controller.findTheSteamUsingSX(v2, v1); //x must be 1 or 0 or 3 element required
            }
        }
        if (chosenQ1.equals("Volume")) {
            if (chosenQ2.equals("Temperature")) {
                steam = controller.findTheSteamUsingTV(v2, v1);
            } else if (chosenQ2.equals("Pressure")) {
                steam = controller.findTheSteamUsingPV(v2, v1);
            } else if (chosenQ2.equals("X")) {
                steam = controller.findTheSteamUsingVX(v1, v2);
            }
        }

        // If none of the conditions matched, you can add an alert or log an error.
        if (steam == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid input combination");
            alert.showAndWait();
            return;
        }












         if (!isThirdInputValid()){
             return;
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

    private boolean isThirdInputValid() {

        if (tF3.isVisible()) {
            if (tF3.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the value of " + comboBox3.getValue());
                return false;
            }
            else if (Integer.parseInt(tF3.getText()) < 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Negative numbers are not allowed");
                return false;
            }
            else if (Integer.parseInt(tF3.getText()) == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText(comboBox3.getValue() +" cannot be zero");
                return false;
            }
        }
        return true;

    }
}
