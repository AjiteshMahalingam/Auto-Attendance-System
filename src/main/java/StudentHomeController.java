import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.BindException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class StudentHomeController implements Initializable {
    long regNum;
    int staffId;
    String courseId;

    private Parent root;
    private Stage stage;
    private Scene scene;

    Connection con;
    PreparedStatement stmt;

    @FXML
    private TextField courseIdTxt;
    @FXML
    private Button joinBtn;
    @FXML
    private Label regNoLabel;
    @FXML
    private TextField staffIdTxt;
    @FXML
    private TableView<TableEntry> table;
    @FXML
    private TableColumn<TableEntry, String> CourseId;
    @FXML
    private TableColumn<TableEntry, Integer> StaffId;
    @FXML
    private TableColumn<TableEntry, Integer> totalClasses;
    @FXML
    private TableColumn<TableEntry, Integer> presentClasses;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/AUTO_ATTENDANCE", "Torvus", "Torvus@1407");
            regNoLabel.setText(this.regNum + "");

            this.stmt = con.prepareStatement("select * from enrolled where StudentId = ?");
            this.stmt.setLong(1, this.regNum);
            ResultSet rs = this.stmt.executeQuery();
            List<TableEntry> list = new ArrayList<>();
            while(rs.next()){
                list.add(new TableEntry(rs.getString("CourseId"), rs.getInt("StaffId"), rs.getInt("presentClasses"), rs.getInt("totalClasses")));
            }
            table.getItems().setAll(list);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    StudentHomeController(long regNum) {
        this.regNum = regNum;
    }

    public void joinClassHandler (ActionEvent e){
        try {
            if(staffIdTxt.getText().trim().equals("") || courseIdTxt.getText().trim().equals("")){
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Class Status");
                warning.setHeaderText("Enter the staff id and course id of the class.");
                warning.showAndWait();
            } else {
                this.courseId = courseIdTxt.getText();
                this.staffId = Integer.parseInt(staffIdTxt.getText());
                this.stmt = this.con.prepareStatement("select * from teaches where CourseId = ? and StaffId = ?");
                this.stmt.setString(1, this.courseId);
                this.stmt.setInt(2, this.staffId);

                ResultSet rs = this.stmt.executeQuery();
                if(rs.next()){
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ClassSession.fxml"));
                    ClassSessionController classController = new ClassSessionController(this.regNum, this.staffId, this.courseId, new Socket("localhost",Integer.parseInt((this.staffId % 100) + this.courseId.substring(3))));
                    loader.setController(classController);
                    root = loader.load();
                    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Class - Auto Attendance System");
                    stage.show();
                } else {
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("Class Status");
                    warning.setHeaderText("Enter the valid staff id and corresponding course id of the staff.");
                    warning.showAndWait();
                }
            }
        } catch (BindException be) {
            be.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Class Status");
            alert.setContentText( be.getMessage() + "\nPort : " + Integer.parseInt((this.staffId % 100) + this.courseId.substring(3)) );
            alert.showAndWait();

        }  catch (ConnectException ce) {
            ce.printStackTrace();
            Alert warning = new Alert(Alert.AlertType.ERROR);
            warning.setTitle("Class Status");
            warning.setHeaderText(ce.getMessage());
            warning.showAndWait();

        }  catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void backHandler(ActionEvent e) {
        try {
            root = FXMLLoader.load(getClass().getResource("StudentLogin.fxml"));
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Student Login / Register - Auto Attendance System");
            stage.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


}
