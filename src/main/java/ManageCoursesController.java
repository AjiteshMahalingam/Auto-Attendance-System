import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManageCoursesController {
    private Parent root;
    private Stage stage;
    private Scene scene;

    @FXML
    private TextField addCourseId;

    @FXML
    private TextField addCourseName;

    @FXML
    private TextField addCredits;

    @FXML
    private TextField removeCourseId;

    public void addCourseHandler(ActionEvent event) {
        String courseId = addCourseId.getText();
        String courseName = addCourseName.getText();
        String credits = addCredits.getText();

        if(courseName.trim().equals("") || courseId.trim().equals("") || credits.trim().equals("")){
            Alert error = new Alert(Alert.AlertType.WARNING);
            error.setTitle("Course status");
            error.setHeaderText("Required");
            error.setContentText("Enter CourseId, Name and credits.");
            error.showAndWait();
        } else {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/AUTO_ATTENDANCE", "Torvus", "Torvus@1407");
                PreparedStatement stmt = con.prepareStatement("insert into courses values (?, ?, ?)");
                stmt.setString(1, courseId);
                stmt.setString(2, courseName);
                stmt.setInt(3, Integer.parseInt(credits));
                stmt.executeUpdate();
                stmt = con.prepareStatement("select * from courses where courseId = ?");
                stmt.setString(1, courseId);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    addCourseId.setText("");
                    addCourseName.setText("");
                    addCredits.setText("");
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Course status");
                    success.setHeaderText("Course added successfully");
                    success.setContentText("Course - " + rs.getString(1) + " created");
                    success.showAndWait();
                } else {
                    Alert failure = new Alert(Alert.AlertType.ERROR);
                    failure.setTitle("Course status");
                    failure.setHeaderText("Add course failed");
                    failure.setContentText("Unable to create the course");
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

    public void removeCourseHandler(ActionEvent event) {
        String courseId = removeCourseId.getText();
        if(courseId.trim().equals("")){
            Alert error = new Alert(Alert.AlertType.WARNING);
            error.setTitle("Course status");
            error.setHeaderText("Required");
            error.setContentText("Enter CourseId");
            error.showAndWait();
        } else {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/AUTO_ATTENDANCE", "Torvus", "Torvus@1407");
                PreparedStatement stmt = con.prepareStatement("delete from courses where CourseId = ?");
                stmt.setString(1, courseId);
                stmt.executeUpdate();
                stmt = con.prepareStatement("select * from courses where CourseId = ?");
                stmt.setString(1, courseId);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    Alert failure = new Alert(Alert.AlertType.ERROR);
                    failure.setTitle("Course status");
                    failure.setHeaderText("Course remove failed");
                    failure.showAndWait();
                } else {
                    removeCourseId.setText("");
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Course status");
                    success.setHeaderText("Course removed successfully");
                    success.showAndWait();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

}
