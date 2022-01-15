import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.opencv.core.Core;

public class MainApplication extends Application {
    public static void main(String[] args) {
        Loader.load(opencv_java.class);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Home - Auto Attendance System");
            primaryStage.setResizable(false);
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> exitHandler(primaryStage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void exitHandler(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setContentText("Do you want to exit ?");
        if(alert.showAndWait().get() == ButtonType.OK){
            stage.close();
        }
    }
}
