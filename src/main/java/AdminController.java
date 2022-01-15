import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {
    private Parent root;
    private Stage stage;
    private Scene scene;

    public void openEnrollmentScene(ActionEvent e) {
        try {
            root = FXMLLoader.load(getClass().getResource("EnrollStudents.fxml"));
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Student Enrollment - Auto Attendance System");
            stage.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void openManageCoursesScene(ActionEvent e) {
        try {
            root = FXMLLoader.load(getClass().getResource("ManageCourses.fxml"));
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Manage Courses - Auto Attendance System");
            stage.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void openManageStaffsScene(ActionEvent e) {
        try {
            root = FXMLLoader.load(getClass().getResource("ManageStaffs.fxml"));
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Manage Staffs - Auto Attendance System");
            stage.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    public void backHandler(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("AdminLogin.fxml"));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
        stage.setTitle("Admin Login - Auto Attendance System");
        stage.show();
    }

}
