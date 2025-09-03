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
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setUberSuper(uberSuper); //inject the UberSuper instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
