import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class StudentLoginController {
    private Parent root;
    private Scene scene;
    private Stage stage;

    @FXML
    private PasswordField LoginPassTxt;
    @FXML
    private TextField loginIdTxt;
    @FXML
    private TextField regDeptTxt;
    @FXML
    private TextField regIdTxt;
    @FXML
    private TextField regNameTxt;
    @FXML
    private PasswordField regPassTxt;
    @FXML
    private TextField regYearTxt;

    public void loginHandler(ActionEvent e) {
        String studentId = loginIdTxt.getText();
        String password = LoginPassTxt.getText();
        if(studentId.trim().equals("") || password.trim().equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Required");
            alert.setContentText("Username / Password required !!");
            alert.showAndWait();
        } else {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/AUTO_ATTENDANCE", "Torvus", "Torvus@1407");

                PreparedStatement stmt = con.prepareStatement("select * from Students where StudentId=? and StudentPwd=?");
                stmt.setString(1, studentId);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()){
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Login Status");
                    success.setHeaderText("Login success");
                    success.showAndWait();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentHome.fxml"));
                    StudentHomeController homeController = new StudentHomeController(Long.parseLong(studentId));
                    loader.setController(homeController);
                    root = loader.load();
                    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Student Home - Auto Attendance System");
                    stage.show();
                } else {
                    Alert fail = new Alert(Alert.AlertType.ERROR);
                    fail.setTitle("Login Status");
                    fail.setHeaderText("Login failed");
                    fail.showAndWait();
                }

                con.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void registerHandler(ActionEvent e) {
        String studentId = regIdTxt.getText();
        String name = regNameTxt.getText();
        String dept = regDeptTxt.getText();
        String gradYear = regYearTxt.getText();
        String password = regPassTxt.getText();

        if(studentId.trim().equals("") || name.trim().equals("") || dept.trim().equals("") || gradYear.trim().equals("") || password.trim().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Register status");
            alert.setHeaderText("Required");
            alert.setContentText("All fields are required !!");
            alert.showAndWait();
        } else {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/AUTO_ATTENDANCE", "Torvus", "Torvus@1407");

                PreparedStatement statement = conn.prepareStatement("insert into Students values (?, ?, ?, ?, ?)");
                statement.setLong(1, Long.parseLong(studentId));
                statement.setString(2, name);
                statement.setString(3, password);
                statement.setString(4, dept);
                statement.setInt(5, Integer.parseInt(gradYear));
                statement.executeUpdate();

                statement = conn.prepareStatement("select * from Students where StudentId = ?");
                statement.setLong(1, Long.parseLong(studentId));
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next()){
                    regIdTxt.setText("");
                    regPassTxt.setText("");
                    regYearTxt.setText("");
                    regDeptTxt.setText("");
                    regNameTxt.setText("");

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterFace.fxml"));
                    root = loader.load();
                    RegisterFaceController controller = loader.getController();
                    controller.getRegNum(Long.parseLong(studentId));

                    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    stage.getScene().setRoot(root);
                    stage.setTitle("Register - Auto Attendance System");
                    stage.show();
                } else {
                    Alert failure = new Alert(Alert.AlertType.ERROR);
                    failure.setTitle("Register status");
                    failure.setHeaderText("Registration failed.");
                    failure.showAndWait();
                }
            } catch (SQLIntegrityConstraintViolationException exception){
                Alert failure = new Alert(Alert.AlertType.ERROR);
                failure.setTitle("Register status");
                failure.setHeaderText("Registration failed.");
                failure.setContentText(exception.getMessage());
                failure.showAndWait();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void backHandler(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
        stage.setTitle("Home - Auto Attendance System");
        stage.show();
    }

}
