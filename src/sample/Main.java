package sample;

import java.awt.Dimension;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Wykresy kNN");
        Scene scena = new Scene(root, screenSize.getWidth()-104, screenSize.getHeight()-160);
        scena.getStylesheets().add("sample/wykres_style.css");
        primaryStage.setScene(scena);
        primaryStage.setMaximized(true);
//        primaryStage.setScene(new Scene(root, screenSize.getWidth()-551, screenSize.getHeight()-310));
        primaryStage.show();
        primaryStage.getIcons().add(new Image("file:kNN-ICON.png"));
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("kNN-ICON.png")));
    }
    public static void main(String[] args) {
        launch(args);
    }
}
