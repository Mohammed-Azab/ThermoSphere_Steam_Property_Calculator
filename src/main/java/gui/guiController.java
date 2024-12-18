package gui;

import Exceptions.NotDefinedException;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import steamTables.Controller;
import steamTables.Steam;

import java.net.URL;
import java.util.ResourceBundle;

public class guiController implements Initializable {

    public Label LP, LT, LV, LU, LH, LS;
    public ComboBox<String> comboBox1, comboBox2,
                            comboBox11, comboBox22, unit1, unit2;
    public ImageView general;
    public ImageView general1;

    public Label type, labelType;
    public TextField tF1, tF2;
    public Button findButton;

    private Controller controller;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tF1.setVisible(true);
        tF2.setVisible(true);
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
        comboBox1.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Volume", "Enthalpy", "Entropy", "Quality", "Phase"));
        comboBox2.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Volume", "Enthalpy", "Entropy", "Quality","Phase"));
        comboBox11.setItems(FXCollections.observableArrayList("Saturated Liquid", "Saturated Vapour", "Saturated Mixture"));
        comboBox22.setItems(FXCollections.observableArrayList("Saturated Liquid", "Saturated Vapour", "Saturated Mixture"));


        comboBox1.valueProperty().addListener((observable, oldValue, newValue) -> {
            unit1.setPromptText("Unit");
            unit1.setVisible(true);
            if (newValue != null){
                if(newValue.equals("Phase") || newValue.equals("Quality")){
                    unit1.setVisible(false);
                }
                else {
                    updateUnitsOptions(unit1, newValue);
                }
            }
            if (!updateComboBoxOptions(comboBox2, newValue)){
                unit2.getItems().clear();
                unit2.setValue(null);
                unit2.setPromptText("Unit");
            }
            if(newValue != null) {
                comboBox11.setVisible(newValue.equals("Phase"));
            }
        });

        comboBox2.valueProperty().addListener((observable, oldValue, newValue) -> {
            unit2.setPromptText("Unit");
            unit2.setVisible(true);
            if (newValue != null){
                if(newValue.equals("Phase") || newValue.equals("Quality")){
                    unit2.setVisible(false);
                }
                else {
                    updateUnitsOptions(unit2, newValue);
                }
            }
            if (newValue != null) {
                if (!updateComboBoxOptions(comboBox1, newValue)){
                    unit1.getItems().clear();
                    unit1.setValue(null);
                    unit1.setPromptText("Unit");
                }
            }
            if(newValue != null) {
                comboBox22.setVisible(newValue.equals("Phase"));
            }
        });

        comboBox11.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("Saturated Mixture")) {
                    comboBox22.setValue("Choose The Phase");
                    comboBox22.setVisible(false);
                    comboBox11.setVisible(false);
                    comboBox1.setValue("Quality");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter the Quality value for the Saturated Mixture");
                    alert.showAndWait();
                }
            }
        });

        comboBox22.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("Saturated Mixture")) {
                    comboBox22.setValue("Choose The Phase");
                    comboBox22.setVisible(false);
                    comboBox11.setVisible(false);
                    comboBox2.setValue("Quality");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter the Quality value for the Saturated Mixture");
                    alert.showAndWait();
                }
            }

        });

    }

    private void updateUnitsOptions(ComboBox<String> unit, String newValue) {
        if (newValue != null) {
            if (newValue.equals("Temperature")) {
                unit.setItems(FXCollections.observableArrayList("Kelvin", "Celsius"));
                unit.setValue("Kelvin");
            }
            else if (newValue.equals("Pressure")) {
                unit.setItems(FXCollections.observableArrayList("KPa", "MPa"));
                unit.setValue("MPa");
            }
            else if (newValue.equals("Volume")) {
                unit.setItems(FXCollections.observableArrayList("m3/kg"));
                unit.setValue("m3/kg");
            }
            else if (newValue.equals("Enthalpy")) {
                unit.setItems(FXCollections.observableArrayList("kJ/kg"));
                unit.setValue("kJ/kg");
            }
            else if (newValue.equals("Entropy")) {
                unit.setItems(FXCollections.observableArrayList("kJ/kg · K"));
                unit.setValue("kJ/kg · K");
            }
        }

    }

    private boolean updateComboBoxOptions(ComboBox<String> comboBox, String selectedOption) {
        String currentOption = comboBox.getValue();
        if ("Temperature".equals(selectedOption)) {
            comboBox.setItems(FXCollections.observableArrayList("Pressure", "Volume", "Enthalpy", "Entropy", "Quality", "Phase"));
        } else if ("Pressure".equals(selectedOption)) {
            comboBox.setItems(FXCollections.observableArrayList("Temperature", "Volume", "Enthalpy", "Entropy", "Quality", "Phase"));
        } else if ("Volume".equals(selectedOption)) {
            comboBox.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Quality", "Phase"));
        }
        else if ("Enthalpy".equals(selectedOption)) {
            comboBox.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Quality", "Phase"));
        }
        else if ("Entropy".equals(selectedOption)) {
            comboBox.setItems(FXCollections.observableArrayList("Temperature", "Pressure" , "Quality", "Phase"));
        }
        else if ("Quality".equals(selectedOption)) {
            comboBox.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Volume", "Enthalpy", "Entropy"));
        }
        else if ("Phase".equals(selectedOption)) {
            comboBox.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Volume", "Enthalpy", "Entropy"));
        }
        return comboBox.getItems().contains(currentOption);
    }

    public void find(MouseEvent mouseEvent) {
        String chosenQ1 = (String) comboBox1.getSelectionModel().getSelectedItem();
        String chosenQ2 = (String) comboBox2.getSelectionModel().getSelectedItem();
        if (chosenQ1 == null  || chosenQ2 == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText("You need to select at least two options");
            alert.showAndWait();
            return;
        }
        String q1 = tF1.getText();
        String q2 = tF2.getText();
        if (!isInputsValid()){
            return;
        }
        double v1 = 0, v2 = 0;
        if (!chosenQ1.equals("Phase")){
            v1 = Double.parseDouble(q1);
        }
        if (!chosenQ2.equals("Phase")){
            v2 = Double.parseDouble(q2);
        }

        Steam steam = new Steam();

        try {
            if (chosenQ1.equals("Temperature")) {
                if (chosenQ2.equals("Pressure")) {
                    steam = controller.findTheSteamUsingTP(v1, v2);
                } else if (chosenQ2.equals("Quality")) {
                    if (!checkQualityValue(v2)) {
                        return;
                    }
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
                } else if (chosenQ2.equals("Quality")) {
                    if (!checkQualityValue(v2)) {
                        return;
                    }
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
                } else if (chosenQ2.equals("Quality")) {
                    if (!checkQualityValue(v2)) {
                        return;
                    }
                    steam = controller.findTheSteamUsingXS(v2, v1);
                }
            }
            if (chosenQ1.equals("Enthalpy")) {
                if (chosenQ2.equals("Temperature")) {
                    steam = controller.findTheSteamUsingTH(v2, v1);
                } else if (chosenQ2.equals("Pressure")) {
                    steam = controller.findTheSteamUsingPH(v2, v1);
                } else if (chosenQ2.equals("Quality")) {
                    if (!checkQualityValue(v2)) {
                        return;
                    }
                    steam = controller.findTheSteamUsingHX(v1, v2);
                }
            }
            if (chosenQ1.equals("Quality")) {
                if (!checkQualityValue(v2)) {
                    return;
                }
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
                } else if (chosenQ2.equals("Quality")) {
                    if (!checkQualityValue(v2)) {
                        return;
                    }
                    steam = controller.findTheSteamUsingVX(v1, v2);
                }
            }
            if (chosenQ1.equals("Phase")) {
                String phase = "";
                if (comboBox11.isVisible()) {
                    phase = comboBox11.getValue();
                } else if (comboBox22.isVisible()) {
                    phase = comboBox22.getValue();
                } else {
                    throw new IllegalArgumentException("Phase is not visible");
                }
                if (phase.equals("Saturated Liquid, \"\", \"\"")) {
                    v1 = 0.0;
                } else if (phase.equals("Saturated Vapour")) {
                    v1 = 1.0;
                }
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
        }
        catch (NotDefinedException nde) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Missing or Incomplete Data");
            alert.setContentText("The specified steam is not defined, or the tables do not contain all the required data.");
            alert.showAndWait();
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














    }

    private boolean checkQualityValue(double v2) {
        if (v2 <=1 && v2 >0){
            return true;
        }
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText("Quality value is not valid, X should be between 0 and 1");
        alert.showAndWait();
        return false;
    }


    private boolean isInputsValid() {
        if (tF1.isVisible()) {
            if (tF1.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the value of " + comboBox1.getValue());
                if (!comboBox1.getValue().equals("Phase")) {
                    alert.showAndWait();
                }
                return false;
            }
            else if (!checkIfNumeric(tF1.getText())) {
                return false;
            }
            else if (Double.parseDouble(tF1.getText()) < 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Negative numbers are not allowed");
                alert.showAndWait();
                return false;
            }
            else if (Double.parseDouble(tF1.getText()) == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText(comboBox1.getValue() +" cannot be zero");
                alert.showAndWait();
                return false;
            }
            else if (unit1.isVisible() && (unit1.getValue() == null) || unit1.getValue().equals("Unit")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please select the unit of " + comboBox1.getValue());
                alert.showAndWait();
            }
        }
        if (tF2.isVisible()) {
            if (tF2.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the value of " + comboBox2.getValue());
                alert.showAndWait();
                return false;
            }
            else if (!checkIfNumeric(tF2.getText())) {
                return false;
            }
            else if (Double.parseDouble(tF2.getText()) < 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Negative numbers are not allowed");
                alert.showAndWait();
                return false;
            }
            else if (Double.parseDouble(tF2.getText()) == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText(comboBox2.getValue() +" cannot be zero");
                alert.showAndWait();
                return false;
            }
            else if (unit2.isVisible() && (unit2.getValue() == null) || unit2.getValue().equals("Unit")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please select the unit of " + comboBox2.getValue());
                alert.showAndWait();
            }
        }



        return true;
    }

    private void isValidNumber(String input) {
        if (!input.matches("^[1-9][0-9]*$")){ // The pattern starts with nonZero element
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText( input +"is not a valid input, Please enter a valid number Format");
            alert.showAndWait();
        }
    }



    public boolean checkIfNumeric(String input) {
        try {
            if (!input.isEmpty()) {
                Double.parseDouble(input.trim());
            }
            return true;
        } catch (Exception exception) {
            showAlert("Invalid Input", "The input must be a valid number.");
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
