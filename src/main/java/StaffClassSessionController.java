import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.net.*;
import  java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class StaffClassSessionController implements Initializable {
    private Parent root;
    private Stage stage;
    private Scene scene;

    int staffId;
    String courseId;
    int port;
    ServerSocket server;

    Connection con ;
    PreparedStatement stmt ;

    Date startTime;
    Date endTime;
    int count = 0;
    boolean classCompleted;
    ArrayList<AttendanceDetails> attendanceList;

    private class AttendanceDetails {
        long regNum;
        long attendance;

        AttendanceDetails(long regNum, long attendance) {
            this.regNum = regNum;
            this.attendance = attendance;
        }
    }

    @FXML
    private Label attendanceLabel;
    @FXML
    private Label classLabel;
    @FXML
    private Label courseIdLabel;
    @FXML
    private Label staffIdLabel;
    @FXML
    private Text classTxt;

    StaffClassSessionController(int staffId, String courseId) {
        this.staffId = staffId;
        this.courseId = courseId;
        this.classCompleted = false;
        this.attendanceList = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/AUTO_ATTENDANCE", "Torvus", "Torvus@1407");

            stmt = con.prepareStatement("update enrolled set totalClasses = totalClasses + 1 where CourseId = ? and StaffId = ?");
            stmt.setString(1, this.courseId);
            stmt.setInt(2, this.staffId);
            stmt.executeUpdate();

            courseIdLabel.setText(courseId);
            staffIdLabel.setText(String.valueOf(staffId));
            classTxt.setText(classTxt.getText() + ">> Class Started at " + new Date() + "\n");
            this.port = Integer.parseInt((staffId % 100) + courseId.substring(3));
            this.server = new ServerSocket(this.port);

            this.startTime = new Date();
            ConnectStudents connect = new ConnectStudents();
            connect.start();

        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    class ConnectStudents extends Thread {
        @Override
        public void run() {
            try {
                while(true) {
                    Socket student = server.accept();

                    ReadData task = new ReadData(student);
                    Thread readThread = new Thread(task);
                    readThread.start();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ReadData implements Runnable {
        Socket socket;

        ReadData(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                ObjectInputStream input = new ObjectInputStream(this.socket.getInputStream());
                Data details = (Data) input.readObject();
                long studentRegNum = details.predictedRegNum;
                long attendance = 0;
                classTxt.setText(classTxt.getText() + ">> New Student added - " +  studentRegNum + "\n");
                while(true) {
                    Data data = (Data) input.readObject();
                    if(data.predictedRegNum == -1) {
                        classTxt.setText(classTxt.getText() + ">> Student left - " + studentRegNum + "\n");
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        output.writeObject(new Data(-1));
                        output.close();
                        markAttendance(studentRegNum, attendance);
                        break;
                    }
                    if (data.predictedRegNum == studentRegNum) {
                        attendance += 1;
                    }
                    System.out.println(data.predictedRegNum + "," + data.time.toString());

                }
                input.close();
                socket.close();
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void endClassHandler(ActionEvent e){
        try{
            this.endTime = new Date();
            this.classCompleted = true;
            server.close();
            long duration = (this.endTime.getTime() - this.startTime.getTime())/1000;
            System.out.println("Class Ended - Duration : " + duration);
            for(AttendanceDetails a : attendanceList){
                System.out.println(a.regNum + "-" + a.attendance);
                a.attendance = (a.attendance*100)/duration;
                System.out.println(a.regNum + "-" + a.attendance);
                stmt = con.prepareStatement("Insert into attendance (CourseId, StaffId, StudentId, classDate, isPresent) values (?, ?, ?, ?, ?)");
                stmt.setString(1, this.courseId);
                stmt.setInt(2, this.staffId);
                stmt.setLong(3, a.regNum);
                stmt.setDate(4, new java.sql.Date(this.startTime.getTime()));
                if(a.attendance >= 50)
                    stmt.setBoolean(5, true);
                else
                    stmt.setBoolean(5, false);
                stmt.executeUpdate();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("StaffHome.fxml"));
            root = loader.load();
            StaffController staffController = loader.getController();
            staffController.getStaffId(this.staffId);
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Staff Home - Auto Attendance System");
            stage.show();
        } catch(SocketException se) {
            System.out.println(se.getMessage());
        } catch (Exception ex){
            ex.printStackTrace();
        }

    }

    synchronized void markAttendance(long studentReg, long attendance){
        attendanceList.add(new AttendanceDetails(studentReg, attendance));
    }
}
