import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class StaffLoginController {
    private Parent root;
    private Stage stage;
    private Scene scene;
    @FXML
    private PasswordField passTxt;

    @FXML
    private TextField staffIdTxt;

    public void loginHandler(ActionEvent e) {
        String staffId = staffIdTxt.getText();
        String password = passTxt.getText();
        if(staffId.trim().equals("") || password.trim().equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Required");
            alert.setContentText("Username / Password required !!");
            alert.showAndWait();
        } else {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/AUTO_ATTENDANCE", "Torvus", "Torvus@1407");

                PreparedStatement stmt = con.prepareStatement("select * from Staffs where StaffId=? and StaffPwd=?");
                stmt.setString(1, staffId);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()){
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Login Status");
                    success.setHeaderText("Login success");
                    success.showAndWait();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("StaffHome.fxml"));
                    root = loader.load();
                    StaffController staffController = loader.getController();
                    staffController.getStaffId(rs.getInt(1));
                    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Staff Home - Auto Attendance System");
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
    public void backHandler(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
        stage.setTitle("Home - Auto Attendance System");
        stage.show();
    }
}
