package sample;

import java.awt.Dimension;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Wykresy kNN");
        primaryStage.setScene(new Scene(root, screenSize.getWidth()-104, screenSize.getHeight()-160));
        primaryStage.setMaximized(true);
        //primaryStage.setScene(new Scene(root, screenSize.getWidth()-551, screenSize.getHeight()-310));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
