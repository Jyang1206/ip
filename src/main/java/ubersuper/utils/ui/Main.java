package ubersuper.utils.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ubersuper.UberSuper;


/**
 * A GUI for UberSuper using FXML.
 */
public class Main extends Application {

    private final UberSuper uberSuper = new UberSuper();

    @Override
    public void start(Stage stage) {
        try {
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            assert ap != null : "MainWindow.fxml should load an AnchorPane";
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            MainWindow controller = fxmlLoader.<MainWindow>getController();
            assert controller != null : "MainWindow.fxml should have a valid controller";
            controller.setUberSuper(uberSuper); //inject the UberSuper instance
            stage.setTitle("UberSuper");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
