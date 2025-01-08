package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Objects;

public class Main extends Application {

    private static MediaPlayer backgroundMediaPlayer;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/MainInterface.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        Image appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icon.jpg")));
        stage.getIcons().add(appIcon);
        stage.setTitle("ThermoSphere");
        stage.resizableProperty().setValue(Boolean.FALSE);
        playBackgroundMusic();
        stage.show();
    }

    public static void playBackgroundMusic() {
        try {
            if (backgroundMediaPlayer == null) {
                String musicFilePath = Objects.requireNonNull(Main.class.getResource(
                        "/sounds/PufinoHarmony.mp3")).toExternalForm();
                Media backgroundMusic = new Media(musicFilePath);
                backgroundMediaPlayer = new MediaPlayer(backgroundMusic);
                backgroundMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the music
            }
            backgroundMediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Failed to load background music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void ensureMusicPlaying() {
        if (backgroundMediaPlayer != null && backgroundMediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            backgroundMediaPlayer.play();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
