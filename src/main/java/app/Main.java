package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/MainInterface.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.resizableProperty().setValue(Boolean.FALSE);


        String musicFile = Objects.requireNonNull(getClass().getResource("/PufinoHarmony.mp3")).toExternalForm();
        try {
            //Media media = new Media(musicFile);
            //MediaPlayer mediaPlayer = new MediaPlayer(media);
            //mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            //mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("The music is not working correctly.");
        }
        stage.show();

    }
    public static void main(String[] args) {
        launch(args);
    }
}

