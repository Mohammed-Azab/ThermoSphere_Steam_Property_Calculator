package gui;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class infoController implements Initializable {
    public ImageView p5,p4,p3,p2,p1;
    public Label t5,t4,t3,t2,t1;
    public ImageView arrowRight, arrowLeft;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        t1.setVisible(true);
        arrowLeft.setDisable(true);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-1);  // Set saturation to -1 for grayscale
        arrowLeft.setEffect(colorAdjust);
    }
    public void backward(MouseEvent mouseEvent) {
      if (t2.isVisible()) {
            p2.setVisible(false);
            t2.setVisible(false);
            p1.setVisible(true);
            t1.setVisible(true);
          ColorAdjust colorAdjust = new ColorAdjust();
          colorAdjust.setSaturation(-1);  // Set saturation to -1 for grayscale
          arrowLeft.setEffect(colorAdjust);
          arrowLeft.setDisable(true);
          arrowRight.setDisable(false);
        }
        else if (t3.isVisible()) {
            p3.setVisible(false);
            t3.setVisible(false);
            p2.setVisible(true);
            t2.setVisible(true);
        }
        else if (t4.isVisible()) {
            p4.setVisible(false);
            t4.setVisible(false);
            p3.setVisible(true);
            t3.setVisible(true);
        }
        else if (t5.isVisible()) {
            p5.setVisible(false);
            t5.setVisible(false);
            p4.setVisible(true);
            t4.setVisible(true);
          arrowRight.setEffect(null);
          arrowRight.setDisable(false);
      }
    }

    public void forward(MouseEvent mouseEvent) {
        if (t1.isVisible()) {
            t1.setVisible(false);
            p1.setVisible(false);
            t2.setVisible(true);
            p2.setVisible(true);
            arrowLeft.setEffect(null);
            arrowLeft.setDisable(false);
        }
        else if (t2.isVisible()) {
            t2.setVisible(false);
            p2.setVisible(false);
            t3.setVisible(true);
            p3.setVisible(true);
        }
        else if (t3.isVisible()) {
            p3.setVisible(false);
            t3.setVisible(false);
            p4.setVisible(true);
            t4.setVisible(true);
        }
        else if (t4.isVisible()) {
            p4.setVisible(false);
            t4.setVisible(false);
            p5.setVisible(true);
            t5.setVisible(true);
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setSaturation(-1);  // Set saturation to -1 for grayscale
            arrowRight.setEffect(colorAdjust);
            arrowRight.setDisable(true);
        }
    }
}
