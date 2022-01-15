import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class StudentEnrollmentController implements Initializable {
    private Parent root;
    private Stage stage;
    private Scene scene;

    Connection conn;
    PreparedStatement stmt;

    @FXML
    private TextField enrollCourseId;
    @FXML
    private TextField enrollStudentId;
    @FXML
    private TextField enrollStaffId;
    @FXML
    private TextField removeCourseId;
    @FXML
    private TextField removeStudentId;
    @FXML
    private TextField removeStaffId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/AUTO_ATTENDANCE", "Torvus", "Torvus@1407");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enrollStudentHandler (ActionEvent e) {
        String studentId = enrollStudentId.getText();
        String courseId = enrollCourseId.getText();
        String staffId = enrollStaffId.getText();

        if(studentId.trim().equals("") || courseId.trim().equals("") || staffId.trim().equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Required");
            alert.setContentText("All fields are required");
            alert.showAndWait();
        } else {
            try {
                this.stmt = this.conn.prepareStatement("select * from teaches where StaffId = ? and CourseId = ?");
                stmt.setInt(1, Integer.parseInt(staffId));
                stmt.setString(2, courseId);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    stmt = conn.prepareStatement("insert into enrolled values (?, ?, 0, ?, 0)");
                    stmt.setString(1, courseId);
                    stmt.setLong(2, Long.parseLong(studentId));
                    stmt.setInt(3, Integer.parseInt(staffId));
                    if(stmt.executeUpdate() == 1){
                        enrollStudentId.setText("");
                        enrollCourseId.setText("");
                        enrollStaffId.setText("");
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Enrollment status");
                        success.setHeaderText("Student enrolled successfully");
                        success.showAndWait();
                    } else {
                        Alert failure = new Alert(Alert.AlertType.ERROR);
                        failure.setTitle("Enrollment status");
                        failure.setHeaderText("Student enrolled failed");
                        failure.showAndWait();
                    }
                } else {
                    Alert failure = new Alert(Alert.AlertType.ERROR);
                    failure.setTitle("Enrollment status");
                    failure.setHeaderText("Student enrolled failed");
                    failure.setContentText("Enter correct staff and course details");
                    failure.showAndWait();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void removeStudentHandler(ActionEvent e) {
        String studentId = removeStudentId.getText();
        String courseId = removeCourseId.getText();
        String staffId = removeStaffId.getText();

        if(studentId.trim().equals("") || courseId.trim().equals("") || staffId.trim().equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Required");
            alert.setContentText("All fields are required");
            alert.showAndWait();
        } else {
            try {
                this.stmt = this.conn.prepareStatement("select * from teaches where StaffId = ? and CourseId = ?");
                stmt.setInt(1, Integer.parseInt(staffId));
                stmt.setString(2, courseId);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    stmt = conn.prepareStatement("delete from enrolled where CourseId = ? and StudentId = ? and StaffId = ?");
                    stmt.setString(1, courseId);
                    stmt.setLong(2, Long.parseLong(studentId));
                    stmt.setInt(3, Integer.parseInt(staffId));
                    if(stmt.executeUpdate() == 1){
                        removeStudentId.setText("");
                        removeCourseId.setText("");
                        removeStaffId.setText("");
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Disenrollment status");
                        success.setHeaderText("Student disenrolled successfully");
                        success.showAndWait();
                    } else {
                        Alert failure = new Alert(Alert.AlertType.ERROR);
                        failure.setTitle("Disenrollment status");
                        failure.setHeaderText("Student disenrolled failed");
                        failure.showAndWait();
                    }
                } else {
                    Alert failure = new Alert(Alert.AlertType.ERROR);
                    failure.setTitle("Disenrollment status");
                    failure.setHeaderText("Student disenrolled failed");
                    failure.setContentText("Enter correct staff and course details");
                    failure.showAndWait();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void backHandler(ActionEvent e) {
        try {
            root = FXMLLoader.load(getClass().getResource("AdminHome.fxml"));
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Admin - Auto Attendance System");
            stage.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
