package gui;

import Exceptions.CannotBeInterpolated;
import Exceptions.MoreInfoNeeded;
import Exceptions.NotDefinedException;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import steamTables.Controller;
import steamTables.Steam;
import steamTables.SteamPhase;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class guiController implements Initializable {

    public Label LP, LT, LV, LU, LH, LS;
    public ComboBox<String> comboBox1, comboBox2, comboBox3,
                            comboBox11, comboBox22, unit1, unit2;
    public ImageView general;
    public ImageView general1;

    public Label type, labelType;
    public TextField tF1, tF2;
    public Button findButton;
    public Button resetButton;
    public Label nOfQ;

    private Controller controller;

    private boolean withUnits = false;
    private boolean isUpdatingComboBox1 = false;
    private boolean isUpdatingComboBox2 = false;

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
        comboBox1.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Volume", "Internal Energy", "Enthalpy", "Entropy", "Quality", "Phase"));
        comboBox2.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Volume", "Internal Energy", "Enthalpy", "Entropy", "Quality","Phase"));
        comboBox11.setItems(FXCollections.observableArrayList(" ","Compressed Liquid","Saturated Liquid", "Saturated Vapour", "Saturated Mixture", "SuperHeated Water"));
        comboBox22.setItems(FXCollections.observableArrayList(" ","Compressed Liquid","Saturated Liquid", "Saturated Vapour", "Saturated Mixture", "SuperHeated Water"));

        tF1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                tF2.requestFocus();
            }
        });

        tF2.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                findAll();
            }
        });

        comboBox1.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdatingComboBox1) {
                return; // Avoid triggering if already in update process
            }
            try {
                isUpdatingComboBox2 = true; // Prevent comboBox2 listener from executing

                if (!comboBox3.isVisible()) {
                    unit1.setVisible(true);
                }
                if (oldValue != null && oldValue.equals("Phase")) {
                    comboBox11.setVisible(true);
                    comboBox11.setValue(" ");
                    comboBox11.setVisible(false);
                }
                if (newValue != null && !comboBox3.isVisible()) {
                    if (newValue.equals("Phase") || newValue.equals("Quality")) {
                        unit1.setVisible(false);
                    } else {
                        updateUnitsOptions(unit1, newValue);
                    }
                }
                if (updateComboBoxOptions(comboBox2, newValue)) {
                    unit2.getItems().clear();
                    unit2.setValue(null);
                    unit2.setPromptText("Unit");
                }
                if (newValue != null) {
                    comboBox11.setVisible(newValue.equals("Phase"));
                }
            } finally {
                isUpdatingComboBox2 = false; // Re-enable comboBox2 listener
            }
        });

        comboBox2.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdatingComboBox2) {
                return; // Avoid triggering if already in update process
            }
            try {
                isUpdatingComboBox1 = true; // Prevent comboBox1 listener from executing

                if (!comboBox3.isVisible()) {
                    unit2.setVisible(true);
                }
                if (oldValue != null && oldValue.equals("Phase")) {
                    comboBox22.setVisible(true);
                    comboBox22.setValue(" ");
                    comboBox22.setVisible(false);
                }
                if (newValue != null && !comboBox3.isVisible()) {
                    if (newValue.equals("Phase") || newValue.equals("Quality")) {
                        unit2.setVisible(false);
                    } else {
                        updateUnitsOptions(unit2, newValue);
                    }
                }
                if (newValue != null) {
                    if (updateComboBoxOptions(comboBox1, newValue)) {
                        unit1.getItems().clear();
                        unit1.setValue(null);
                        unit1.setPromptText("Unit");
                    }
                }
                if (newValue != null) {
                    comboBox22.setVisible(newValue.equals("Phase"));
                }
            } finally {
                isUpdatingComboBox1 = false; // Re-enable comboBox1 listener
            }
        });

        comboBox11.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("Saturated Mixture")) {
                    comboBox1.setValue("Quality");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter the Quality value for the Saturated Mixture");
                    alert.showAndWait();
                }
                else if (newValue.equals("Compressed Liquid")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter two more Qualities");
                    alert.showAndWait();
                    updateAccordingCompOrSuperHeated(SteamPhase.getPhase("Compressed Liquid"));
                }
                else if (newValue.equals("SuperHeated Water")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter two more Qualities");
                    alert.showAndWait();
                    updateAccordingCompOrSuperHeated(SteamPhase.getPhase("Superheated Water"));
                }
            }
        });

        comboBox22.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("Saturated Mixture")) {
                    comboBox22.setVisible(false);
                    comboBox11.setVisible(false);
                    comboBox2.setValue("Quality");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter the Quality value for the Saturated Mixture");
                    alert.showAndWait();
                }
                else if (newValue.equals("Compressed Liquid")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter two more Qualities");
                    alert.showAndWait();
                    updateAccordingCompOrSuperHeated(SteamPhase.getPhase("Compressed Liquid"));
                }
                else if (newValue.equals("SuperHeated Water")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter two more Qualities");
                    alert.showAndWait();
                    updateAccordingCompOrSuperHeated(SteamPhase.getPhase("Superheated Water"));
                }
            }

        });

    }

    private void updateUnitsOptions(ComboBox<String> unit, String newValue) {
        if (newValue != null && unit.isVisible() && unit != null) {
            if (newValue.equals("Temperature")) {
                unit.setItems(FXCollections.observableArrayList("Kelvin", "Celsius"));
                unit.setValue("Celsius");
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
            else if (newValue.equals("Internal Energy")) {
                unit.setItems(FXCollections.observableArrayList("kJ/kg"));
                unit.setValue("kJ/kg");
            }
        }

    }

    private boolean updateComboBoxOptions(ComboBox<String> comboBox, String selectedOption) {
        String currentOption = comboBox.getValue();
        try {
            if ("Temperature".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Pressure", "Volume", "Internal Energy", "Enthalpy", "Entropy", "Quality", "Phase"));
            } else if ("Pressure".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Temperature", "Volume", "Internal Energy", "Enthalpy", "Entropy", "Quality", "Phase"));
            } else if ("Volume".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Quality", "Phase"));
            } else if ("Enthalpy".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Quality", "Phase"));
            } else if ("Entropy".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Quality", "Phase"));
            } else if ("Quality".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Volume","Internal Energy", "Enthalpy", "Entropy"));
            } else if ("Phase".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Temperature", "Pressure","Volume", "Internal Energy", "Enthalpy", "Entropy"));
            } else if ("Internal Energy".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Volume", "Quality", "Phase"));
            } else if ("Pressure MPa".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Temperature C", "Internal Energy kJ/kg", "Volume m3/kg", "Enthalpy kJ/kg", "Entropy kJ/kg · K"));
            } else if ("Temperature C".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Pressure MPa", "Volume m3/kg", "Internal Energy kJ/kg", "Enthalpy kJ/kg", "Entropy kJ/kg · K"));
            } else if ("Internal Energy kJ/kg".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Pressure MPa", "Temperature C", "Volume m3/kg", "Enthalpy kJ/kg", "Entropy kJ/kg · K"));
            } else if ("Enthalpy kJ/kg".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Pressure MPa", "Temperature C", "Volume m3/kg", "Internal Energy kJ/kg", "Entropy kJ/kg · K"));
            } else if ("Entropy kJ/kg · K".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Pressure MPa", "Temperature C", "Volume m3/kg", "Internal Energy kJ/kg", "Enthalpy kJ/kg"));
            }
            else if ("Volume m3/kg".equals(selectedOption)) {
                comboBox.setItems(FXCollections.observableArrayList("Pressure MPa", "Temperature C", "Internal Energy kJ/kg", "Enthalpy kJ/kg", "Entropy kJ/kg · K"));
            }
            return !comboBox.getItems().contains(currentOption);
        } catch (StackOverflowError e) {
            resetALl();
            System.err.println("Stack Overflow Error");
        }
        return !comboBox.getItems().contains(currentOption);
    }

    private void updateAccordingCompOrSuperHeated(SteamPhase state) {
        comboBox3.setVisible(true); // needs to be before the Listener
        comboBox1.setValue("Pressure MPa");
        comboBox2.setValue("Temperature C");
        comboBox1.setValue(null);
        comboBox2.setValue(null);
        tF1.clear();
        tF2.clear();
        comboBox11.setVisible(false);
        comboBox22.setVisible(false);
        unit1.setVisible(false);
        unit2.setVisible(false);
        comboBox3.setValue(state.toString());
        nOfQ.setText("3");
        resetButton.setVisible(true);
    }

    public void find(MouseEvent mouseEvent) {
        findAll();
    }
    public void info(MouseEvent mouseEvent) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/Info.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            Image appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.jpg")));
            stage.getIcons().add(appIcon);
            stage.setTitle("Info");
            stage.resizableProperty().setValue(Boolean.FALSE);
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Something went wrong, please try again");
            alert.showAndWait();
        }
    }

    private void findAll() {
        withUnits = false;
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
        else {
            if (comboBox11.isVisible() && ((String) comboBox11.getSelectionModel().getSelectedItem())!=null &&
                    ((String) comboBox11.getSelectionModel().getSelectedItem()).equals(" ")){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention");
                alert.setHeaderText(null);
                alert.setContentText("PLease select the phase");
                alert.showAndWait();
                return;
            }
        }
        if (!chosenQ2.equals("Phase")){
            v2 = Double.parseDouble(q2);
        }
        else {
            if (comboBox22.isVisible() && ((String) comboBox22.getSelectionModel().getSelectedItem())!=null &&
                    ((String) comboBox22.getSelectionModel().getSelectedItem()).equals(" ")){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention");
                alert.setHeaderText(null);
                alert.setContentText("PLease select the phase");
                alert.showAndWait();
                return;
            }
        }

        Steam steam = new Steam();
        withUnits = comboBox3.isVisible();

        if (chosenQ1.equals("Pressure")){
            v1 = unit1.getValue().equals("MPa") ? v1 * 1000 : v1;
        }
        else if (chosenQ2.equals("Pressure")){
            v2 = unit2.getValue().equals("MPa") ? v2 * 1000 : v2;
        }

        try {
            if (chosenQ1.equals("Temperature")) {
                v1 = unit1.getValue().equals("Celsius")? v1 : v1-273;
                if (chosenQ2.equals("Pressure")) {
                    steam = controller.findTheSteamUsingTP(v1, v2);
                } else if (chosenQ2.equals("Quality")) {
                    if (checkQualityValue(v2)) {
                        return;
                    }
                    steam = controller.findTheSteamUsingTX(v1, v2);
                } else if (chosenQ2.equals("Volume")) {
                    steam = controller.findTheSteamUsingTV(v1, v2);
                }else if (chosenQ2.equals("Internal Energy")) {
                    steam = controller.findTheSteamUsingTU(v1,v2 );
                }
                else if (chosenQ2.equals("Enthalpy")) {
                    steam = controller.findTheSteamUsingTH(v1, v2);
                } else if (chosenQ2.equals("Entropy")) {
                    steam = controller.findTheSteamUsingTS(v1, v2);
                }
                else if (chosenQ2.equals("Phase")) {
                    steam = controller.findTheSteamUsingTPhase(v1,SteamPhase.getPhase(comboBox22.getValue()));
                }
            }
            if (chosenQ1.equals("Pressure")) {
                if (chosenQ2.equals("Temperature")) {
                    v2 = unit2.getValue().equals("Celsius")? v2 : v2-273;
                    steam = controller.findTheSteamUsingTP(v2, v1);
                } else if (chosenQ2.equals("Quality")) {
                    if (checkQualityValue(v2)) {
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
                else if (chosenQ2.equals("Phase")) {
                    steam = controller.findTheSteamUsingPPhase(v1, SteamPhase.getPhase(comboBox22.getValue()));
                }
                else if (chosenQ2.equals("Internal Energy")) {
                    steam = controller.findTheSteamUsingPU(v1,v2 );
                }
            }
            if (chosenQ1.equals("Entropy")) {
                if (chosenQ2.equals("Temperature")) {
                    v2 = unit2.getValue().equals("Celsius")? v2 : v2-273;
                    steam = controller.findTheSteamUsingTS(v2, v1);
                } else if (chosenQ2.equals("Pressure")) {
                    steam = controller.findTheSteamUsingPS(v2, v1);
                } else if (chosenQ2.equals("Quality")) {
                    if (checkQualityValue(v2)) {
                        return;
                    }
                    steam = controller.findTheSteamUsingXS(v2, v1);
                }
                else if (chosenQ2.equals("Phase")) {
                    steam = controller.findTheSteamUsingSPhase(v1, SteamPhase.getPhase(comboBox22.getValue()));
                }
                else if (chosenQ2.equals("Internal Energy")) {
                    steam = controller.findTheSteamUsingUS(v2, v1);
                }
                else if (chosenQ2.equals("Enthalpy")) {
                    steam = controller.findTheSteamUsingHS(v1, v2);
                }
                else if (chosenQ2.equals("Volume")) {
                    steam = controller.findTheSteamUsingVS(v2, v1);
                }
            }
            if (chosenQ1.equals("Enthalpy")) {
                if (chosenQ2.equals("Temperature")) {
                    v2 = unit2.getValue().equals("Celsius")? v2 : v2-273;
                    steam = controller.findTheSteamUsingTH(v2, v1);
                } else if (chosenQ2.equals("Pressure")) {
                    steam = controller.findTheSteamUsingPH(v2, v1);
                } else if (chosenQ2.equals("Quality")) {
                    if (checkQualityValue(v2)) {
                        return;
                    }
                    steam = controller.findTheSteamUsingHX(v1, v2);
                }
                else if (chosenQ2.equals("Phase")) {
                    steam = controller.findTheSteamUsingHPhase(v1, SteamPhase.getPhase(comboBox22.getValue()));
                }
                else if (chosenQ2.equals("Internal Energy")) {
                    steam = controller.findTheSteamUsingUH(v2, v1);
                }
                else if (chosenQ2.equals("Entropy")) {
                    steam = controller.findTheSteamUsingHS(v1, v2);
                }
                else if (chosenQ2.equals("Volume")) {
                    steam = controller.findTheSteamUsingVH(v2, v1);
                }
            }
            if (chosenQ1.equals("Quality")) {
                if (checkQualityValue(v2)) {
                    return;
                }
                if (chosenQ2.equals("Temperature")) {
                    v2 = unit2.getValue().equals("Celsius")? v2 : v2-273;
                    steam = controller.findTheSteamUsingTX(v2, v1);
                } else if (chosenQ2.equals("Pressure")) {
                    steam = controller.findTheSteamUsingPX(v2, v1);
                }else if (chosenQ2.equals("Internal Energy")) {
                    steam = controller.findTheSteamUsingUX(v2,v1 );
                }
                else if (chosenQ2.equals("Volume")) {
                    steam = controller.findTheSteamUsingVX(v2, v1);
                } else if (chosenQ2.equals("Enthalpy")) {
                    steam = controller.findTheSteamUsingHX(v2, v1);
                } else if (chosenQ2.equals("Entropy")) {
                    steam = controller.findTheSteamUsingSX(v2, v1); //x must be 1 or 0 or 3 element required
                }
            }
            if (chosenQ1.equals("Volume")) {
                if (chosenQ2.equals("Temperature")) {
                    v2 = unit2.getValue().equals("Celsius")? v2 : v2-273;
                    steam = controller.findTheSteamUsingTV(v2, v1);
                } else if (chosenQ2.equals("Pressure")) {
                    steam = controller.findTheSteamUsingPV(v2, v1);
                }else if (chosenQ2.equals("Internal Energy")) {
                    steam = controller.findTheSteamUsingUV(v2,v1);
                } else if (chosenQ2.equals("Quality")) {
                    if (checkQualityValue(v2)) {
                        return;
                    }
                    steam = controller.findTheSteamUsingVX(v1, v2);
                }
                else if (chosenQ2.equals("Phase")) {
                    steam = controller.findTheSteamUsingVPhase(v1, SteamPhase.getPhase(comboBox22.getValue()));
                }
                else if (chosenQ2.equals("Entropy")) {
                    steam = controller.findTheSteamUsingVS(v1, v2);
                }
                else if (chosenQ2.equals("Enthalpy")) {
                    steam = controller.findTheSteamUsingVH(v1, v2);
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
                SteamPhase steamPhase = SteamPhase.getPhase(phase);
                if (chosenQ2.equals("Temperature")) {
                    v2 = unit2.getValue().equals("Celsius")? v2 : v2-273;
                    steam = controller.findTheSteamUsingTPhase(v2, steamPhase);
                } else if (chosenQ2.equals("Pressure")) {
                    steam = controller.findTheSteamUsingPPhase(v2, steamPhase);
                } else if (chosenQ2.equals("Volume")) {
                    steam = controller.findTheSteamUsingVPhase(v2, steamPhase);
                } else if (chosenQ2.equals("Enthalpy")) {
                    steam = controller.findTheSteamUsingHPhase(v2, steamPhase);
                } else if (chosenQ2.equals("Entropy")) {
                    steam = controller.findTheSteamUsingSPhase(v2, steamPhase); //x must be 1 or 0 or 3 element required
                }
            }
            if (chosenQ1.equals("Internal Energy")) {
                if (chosenQ2.equals("Temperature")) {
                    v2 = unit2.getValue().equals("Celsius")? v2 : v2-273;
                    steam = controller.findTheSteamUsingTU(v2, v1);
                }
                else if (chosenQ2.equals("Pressure")) {
                    steam = controller.findTheSteamUsingPU(v2, v1);
                }
                else if (chosenQ2.equals("Quality")) {
                    if (checkQualityValue(v2)) {
                        return;
                    }
                    steam = controller.findTheSteamUsingUX(v1, v2);
                } else if (chosenQ2.equals("Volume")) {
                    steam = controller.findTheSteamUsingUV(v1, v2);
                } else if (chosenQ2.equals("Enthalpy")) {
                    steam = controller.findTheSteamUsingUH(v1, v2);
                } else if (chosenQ2.equals("Entropy")) {
                    steam = controller.findTheSteamUsingUS(v1, v2);
                }
                else if (chosenQ2.equals("Phase")) {
                    steam = controller.findTheSteamUsingUPhase(v1, SteamPhase.getPhase(comboBox22.getValue()));
                }
            }
            if (chosenQ1.equals("Pressure MPa")) {
                SteamPhase steamPhase = SteamPhase.getPhase(comboBox3.getValue());
                if (chosenQ2.equals("Temperature C")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,0,1,steamPhase);
                }
                else if (chosenQ2.equals("Volume m3/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,0,2,steamPhase);
                }
                else if (chosenQ2.equals("Internal Energy kJ/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,0,3,steamPhase);
                }
                else if (chosenQ2.equals("Enthalpy kJ/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,0,4,steamPhase);
                }
                else if (chosenQ2.equals("Entropy kJ/kg · K")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,0,5,steamPhase);
                }
            }
            else if (chosenQ1.equals("Temperature C")) {
                SteamPhase steamPhase = SteamPhase.getPhase(comboBox3.getValue());
                if (chosenQ2.equals("Pressure MPa")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,1,0,steamPhase);
                }
                else if (chosenQ2.equals("Volume m3/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,1,2,steamPhase);
                }
                else if (chosenQ2.equals("Internal Energy kJ/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,1,3,steamPhase);
                }
                else if (chosenQ2.equals("Enthalpy kJ/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,1,4,steamPhase);
                }
                else if (chosenQ2.equals("Entropy kJ/kg · K")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,1,5,steamPhase);
                }
            }
            else if (chosenQ1.equals("Volume m3/kg")) {
                SteamPhase steamPhase = SteamPhase.getPhase(comboBox3.getValue());
                if (chosenQ2.equals("Pressure MPa")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,2,0,steamPhase);
                }
                else if (chosenQ2.equals("Temperature C")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,2,1,steamPhase);
                }
                else if (chosenQ2.equals("Internal Energy kJ/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,2,3,steamPhase);
                }
                else if (chosenQ2.equals("Enthalpy kJ/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,2,4,steamPhase);
                }
                else if (chosenQ2.equals("Entropy kJ/kg · K")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,2,5,steamPhase);
                }
            }
            else if (chosenQ1.equals("Internal Energy kJ/kg")) {
                SteamPhase steamPhase = SteamPhase.getPhase(comboBox3.getValue());
                if (chosenQ2.equals("Pressure MPa")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,3,0,steamPhase);
                }
                else if (chosenQ2.equals("Temperature C")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,3,1,steamPhase);
                }
                else if (chosenQ2.equals("Volume m3/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,3,2,steamPhase);
                }
                else if (chosenQ2.equals("Enthalpy kJ/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,3,4,steamPhase);
                }
                else if (chosenQ2.equals("Entropy kJ/kg · K")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,3,5,steamPhase);
                }
            }
            else if (chosenQ1.equals("Enthalpy kJ/kg")) {
                SteamPhase steamPhase = SteamPhase.getPhase(comboBox3.getValue());
                if (chosenQ2.equals("Pressure MPa")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,4,0,steamPhase);
                }
                else if (chosenQ2.equals("Temperature C")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,4,1,steamPhase);
                }
                else if (chosenQ2.equals("Volume m3/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,4,2,steamPhase);
                }
                else if (chosenQ2.equals("Internal Energy kJ/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,4,3,steamPhase);
                }
                else if (chosenQ2.equals("Entropy kJ/kg · K")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,4,5,steamPhase);
                }
            }
            else if (chosenQ1.equals("Entropy kJ/kg · K")) {
                SteamPhase steamPhase = SteamPhase.getPhase(comboBox3.getValue());
                if (chosenQ2.equals("Pressure MPa")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,5,0,steamPhase);
                }
                else if (chosenQ2.equals("Temperature C")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,5,1,steamPhase);
                }
                else if (chosenQ2.equals("Volume m3/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,5,2,steamPhase);
                }
                else if (chosenQ2.equals("Internal Energy kJ/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,5,3,steamPhase);
                }
                else if (chosenQ2.equals("Enthalpy kJ/kg")) {
                    steam = controller.findTheSuperHeatedSteamOrCompressedLiquid(v1,v2,5,4,steamPhase);
                }
            }
        }
        catch (NotDefinedException | CannotBeInterpolated nde) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Missing or Incomplete Data");
            alert.setContentText("The specified steam is not defined, or the tables do not contain all the required data.");
            alert.showAndWait();
            return;
        }
        catch (MoreInfoNeeded moreInfoNeeded){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Missing or Incomplete Data");
            alert.setContentText("A third Quantity is needed");
            alert.showAndWait();
            return;
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error occured");
            alert.setHeaderText("Missing or Incomplete Data");
            alert.setContentText("Please try again");
            resetALl();
            alert.showAndWait();
            return;
        }

        if (steam == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid input combination");
            alert.showAndWait();
            return;
        }
        showResults(steam);

    }

    private void showResults(Steam steam) {
        String pressure = withUnits? ""+steam.getP() : ""+steam.getP()/1000;
        LP.setText(pressure);
        LT.setText(""+steam.getT());
        LV.setText(""+steam.getV());
        LU.setText(""+steam.getU());
        LH.setText(""+steam.getH());
        LS.setText(""+steam.getS());
        LP.setVisible(true);
        LT.setVisible(true);
        LV.setVisible(true);
        LU.setVisible(true);
        LH.setVisible(true);
        LS.setVisible(true);
        general1.setVisible(true);
        general.setVisible(true);
        type.setVisible(true);
        labelType.setVisible(true);
        String typeOfSteam = steam.getSteamPhase().toString();
        typeOfSteam = steam.getSteamPhase()== SteamPhase.SaturatedMixture? typeOfSteam+", X = "+steam.getX():typeOfSteam ;
        type.setText(typeOfSteam);

    }

    private boolean checkQualityValue(double v2) {
        if (v2 <0 || v2 >1){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Quality value is not valid, X should be between 0 and 1");
            alert.showAndWait();
            return true;
        }
        return false;
    }


    private boolean isInputsValid() {
        if (!comboBox1.getValue().equals("Phase") && tF1.isVisible()) {
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
            else if (!checkIfNumeric(tF1.getText(),comboBox1.getValue())) {
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
            else if (Double.parseDouble(tF1.getText()) == 0 && !comboBox1.getValue().equals("Quality")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText(comboBox1.getValue() +" cannot be zero");
                if (!comboBox1.getValue().equals("Temperature")) {
                    alert.showAndWait();
                    return false;
                }
            }
            else if (unit1.isVisible() && ((unit1.getValue() == null) || unit1.getValue().equals("Unit"))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please select the unit of " + comboBox1.getValue());
                alert.showAndWait();
            }
        }
        if (!comboBox2.getValue().equals("Phase") && tF2.isVisible()) {
            if (tF2.getText().isEmpty() && !comboBox2.getValue().equals("Phase")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Please enter the value of " + comboBox2.getValue());
                alert.showAndWait();
                return false;
            }
            else if (!checkIfNumeric(tF2.getText(), comboBox2.getValue())) {
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
            else if (Double.parseDouble(tF2.getText()) == 0 && !comboBox2.getValue().equals("Quality")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText(comboBox2.getValue() +" cannot be zero");
                if (!comboBox2.getValue().equals("Temperature")) {
                    alert.showAndWait();
                    return false;
                }
            }
            else if (unit2.isVisible() && ((unit2.getValue() == null) || unit2.getValue().equals("Unit"))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please select the unit of " + comboBox2.getValue());
                alert.showAndWait();
            }
        }



        return true;
    }


    private boolean checkIfNumeric(String input, String quantity) {
        if (input.charAt(0) == '0' && input.length() > 1 && input.charAt(1) != '.') {
            showAlert("Leading zeros are not allowed.");
            return false;
        }
        try {
            if (!input.isEmpty()) {
                Double.parseDouble(input.trim());
            }
            return true;
        } catch (Exception exception) {
            showAlert("Invalid Input", "The value of The " + quantity + " must be a valid number.");
            return false;
        }
    }

    private void showAlert(String title ,String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void reset(MouseEvent mouseEvent) { // worked
        resetALl();
    }

    private void resetALl() {
        comboBox3.setVisible(false);
        unit1.setVisible(true);
        unit2.setVisible(true);
        comboBox1.setVisible(true);
        comboBox2.setVisible(true);
        comboBox11.setVisible(false);
        comboBox22.setVisible(false);
        tF1.clear();
        tF2.clear();
        comboBox1.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Volume", "Internal Energy", "Enthalpy", "Entropy", "Quality", "Phase"));
        comboBox2.setItems(FXCollections.observableArrayList("Temperature", "Pressure", "Volume", "Internal Energy", "Enthalpy", "Entropy", "Quality","Phase"));
        resetButton.setVisible(false);
        nOfQ.setText("2");
        comboBox11.setVisible(true);
        comboBox11.setValue(" ");
        comboBox11.setVisible(false);
        comboBox22.setVisible(true);
        comboBox22.setValue(" ");
        comboBox22.setVisible(false);
    }
}
