import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button staffBtn;

    @FXML
    private Button studentBtn;

    public void openStaffLogin(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("StaffLogin.fxml"));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
        stage.setTitle("Staff Login - Auto Attendance System");
        stage.show();
    }

    public void openStudentLogin(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("StudentLogin.fxml"));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
        stage.setTitle("Student Login / Register - Auto Attendance System");
        stage.show();
    }

    public void openAdminLogin(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("AdminLogin.fxml"));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
        stage.setTitle("Admin Login - Auto Attendance System");
        stage.show();
    }

    public void exitHandler(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setContentText("Do you want to exit ?");
        if(alert.showAndWait().get() == ButtonType.OK){
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.close();
        }
    }
}
