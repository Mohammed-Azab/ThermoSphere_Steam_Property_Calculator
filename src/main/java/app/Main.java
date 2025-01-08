package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception { // need to add the X in GUi
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/MainInterface.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.resizableProperty().setValue(Boolean.FALSE);
        playBackgroundMusic();
        stage.show();
    }   

    public static void playBackgroundMusic() {
        try {
            String musicFilePath = Objects.requireNonNull(Main.class.getResource(
                    "/PufinoHarmony.mp3")).toExternalForm();
            Media backgroundMusic = new Media(musicFilePath);
            MediaPlayer backgroundMediaPlayer = new MediaPlayer(backgroundMusic);
            backgroundMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the music
            backgroundMediaPlayer.play();
        }
        catch (Exception e) {
            System.err.println("Failed to load background music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
