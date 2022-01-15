import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.ResourceBundle;

public class StaffController{
    int staffId;
    String staffName;
    String dept;
    Date date;

    private Parent root;
    private Stage stage;
    private Scene scene;

    @FXML
    private TextField courseId;
    @FXML
    private Label dateLabel;
    @FXML
    private Label deptLabel;
    @FXML
    private Label staffIdLabel;
    @FXML
    private Label staffNameLabel;

    public void getStaffId(int staffId){
        try {
            this.staffId = staffId;
            staffIdLabel.setText(String.valueOf(this.staffId));

            this.date = new Date();

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/AUTO_ATTENDANCE", "Torvus", "Torvus@1407");
            PreparedStatement stmt = con.prepareStatement("select * from staffs where StaffId = ?");
            stmt.setInt(1, this.staffId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            this.staffName = rs.getString(2);
            this.dept = rs.getString(3);
            dateLabel.setText(this.date.toString());
            deptLabel.setText(this.dept);
            staffIdLabel.setText(String.valueOf(this.staffId));
            staffNameLabel.setText(this.staffName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startClassHandler(ActionEvent e) {
        try {
            String course = courseId.getText();
            if(course.trim().equals("")){
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Class Status");
                warning.setHeaderText("Enter the course id of the class.");
                warning.showAndWait();
            } else {
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Class Status");
                success.setHeaderText("Class started and students can join at port " + Integer.parseInt((staffId % 100) + course.substring(3)));
                success.showAndWait();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("StaffClass.fxml"));
                StaffClassSessionController classController = new StaffClassSessionController(this.staffId, course);
                loader.setController(classController);
                root = loader.load();
                stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Staff Class - Auto Attendance System");
                stage.show();
            }
        } catch(Exception exception) {
            exception.printStackTrace();
        }

    }

    public void backHandler(ActionEvent e) {
        try {
            root = FXMLLoader.load(getClass().getResource("StaffLogin.fxml"));
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Staff Login - Auto Attendance System");
            stage.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
