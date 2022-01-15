import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ManageStaffsController implements Initializable {
    private Parent root;
    private Stage stage;
    private Scene scene;

    Connection conn;
    PreparedStatement stmt;
    ResultSet rs;

    @FXML
    private TextField addCourseId;
    @FXML
    private TextField addCourseStaffId;
    @FXML
    private TextField addDept;
    @FXML
    private TextField addStaffId;
    @FXML
    private TextField addStaffName;
    @FXML
    private PasswordField addStaffPwd;
    @FXML
    private TextField removeCourseId;
    @FXML
    private TextField removeCourseStaffId;
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

    public void addCourseHandler(ActionEvent event) {
        try {
            String staffId = addCourseStaffId.getText();
            String courseId = addCourseId.getText();

            if (staffId.trim().equals("") || courseId.trim().equals("")){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Required");
                alert.setHeaderText("All fields are required");
                alert.showAndWait();
            } else {
                this.stmt = this.conn.prepareStatement("insert into teaches values (?, ?)");
                this.stmt.setInt(2, Integer.parseInt(staffId));
                this.stmt.setString(1, courseId);
                if(this.stmt.executeUpdate() == 1){
                    addCourseId.setText("");
                    addCourseStaffId.setText("");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Add Course Status");
                    alert.setHeaderText("Course added successfully.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Add Course Status");
                    alert.setHeaderText("Unable to add course");
                    alert.showAndWait();
                }
            }
        } catch (SQLDataException se) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add Course Status");
            alert.setHeaderText(se.getMessage());
            alert.showAndWait();
        } catch (Exception  e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add Course Status");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void addStaffHandler(ActionEvent event) {
        try {
            String staffId = addStaffId.getText();
            String staffName = addStaffName.getText();
            String staffPwd = addStaffPwd.getText();
            String staffDept = addDept.getText();

            if (staffId.trim().equals("") || staffName.trim().equals("") || staffPwd.trim().equals("") || staffDept.trim().equals("")){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Required");
                alert.setHeaderText("All fields are required");
                alert.showAndWait();
            } else {
                this.stmt = this.conn.prepareStatement("insert into staffs values (?, ?, ?, ?)");
                this.stmt.setInt(1, Integer.parseInt(staffId));
                this.stmt.setString(2, staffName);
                this.stmt.setString(3, staffPwd);
                this.stmt.setString(4, staffDept);
                if(this.stmt.executeUpdate() == 1){
                    addStaffId.setText("");
                    addStaffName.setText("");
                    addStaffPwd.setText("");
                    addDept.setText("");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Add Staff Status");
                    alert.setHeaderText("Staff " + staffId + " added successfully.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Add Staff Status");
                    alert.setHeaderText("Unable to add staff");
                    alert.showAndWait();
                }
            }
        } catch (SQLDataException se) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add Staff Status");
            alert.setHeaderText(se.getMessage());
            alert.showAndWait();
        } catch (Exception  e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add Staff Status");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
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
        try {
            String staffId = removeCourseStaffId.getText();
            String courseId = removeCourseId.getText();

            if (staffId.trim().equals("") || courseId.trim().equals("")){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Required");
                alert.setHeaderText("All fields are required");
                alert.showAndWait();
            } else {
                this.stmt = this.conn.prepareStatement("delete from teaches where CourseId = ? and StaffId = ?");
                this.stmt.setInt(2, Integer.parseInt(staffId));
                this.stmt.setString(1, courseId);
                if(this.stmt.executeUpdate() == 1){
                    removeCourseStaffId.setText("");
                    removeCourseId.setText("");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Remove Course Status");
                    alert.setHeaderText("Course removed successfully.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Remove Course Status");
                    alert.setHeaderText("Unable to remove course");
                    alert.showAndWait();
                }
            }
        } catch (SQLDataException se) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Remove Course Status");
            alert.setHeaderText(se.getMessage());
            alert.showAndWait();
        } catch (Exception  e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Remove Course Status");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void removeStaffHandler(ActionEvent event) {
        try {
            String staffId = removeStaffId.getText();

            if (staffId.trim().equals("")){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Required");
                alert.setHeaderText("All fields are required");
                alert.showAndWait();
            } else {
                this.stmt = this.conn.prepareStatement("delete from staffs where StaffId = ?");
                this.stmt.setInt(1, Integer.parseInt(staffId));
                if(this.stmt.executeUpdate() == 1){
                    removeStaffId.setText("");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Remove Staff Status");
                    alert.setHeaderText("Staff " + staffId + " removed successfully.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Remove Staff Status");
                    alert.setHeaderText("Unable to remove staff");
                    alert.showAndWait();
                }
            }
        } catch (SQLDataException se) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Remove Staff Status");
            alert.setHeaderText(se.getMessage());
            alert.showAndWait();
        } catch (Exception  e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Remove Staff Status");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
        }
    }

}
