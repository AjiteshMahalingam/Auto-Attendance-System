import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class RegisterFaceController implements Initializable {
    private Parent root;
    private Scene scene;
    private Stage stage;
    private VideoCapture capture;
    private Mat frame = new Mat();
    private MatOfByte mem = new MatOfByte();
    private DaemonThread thread;
    private InputStream[] capturedImages;
    private int capturedCount;
    private long regNum;
    private Rect faceCoord;

    @FXML
    private ImageView regImgView;
    @FXML
    private Button captureBtn;
    @FXML
    private Button submitBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        capture = new VideoCapture(0);
        capturedCount = 0;
        capturedImages = new InputStream[10];
        thread = new DaemonThread();
        Thread t = new Thread(thread);
        t.setDaemon(true);
        thread.runnable = true;
        t.start();
        submitBtn.setDisable(true);
    }

    public void captureImage(ActionEvent e){
        if(capturedCount < 10) {
            if(capture.isOpened()){
                Mat capturedFrame = new Mat();
                MatOfByte capturedMem = new MatOfByte();
                capture.retrieve(capturedFrame);
                Mat faceCrop = new Mat(capturedFrame, faceCoord);
                Imgcodecs.imencode(".jpg", faceCrop, capturedMem);
                byte[] byteArray = capturedMem.toArray();
                capturedImages[capturedCount] = new ByteArrayInputStream(byteArray);
                capturedCount += 1;
            }
        }
    }

    public void getRegNum(long regNum) {
        this.regNum = regNum;
    }

    public void submitImages(ActionEvent e){
        try {
            boolean flag = true;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/AUTO_ATTENDANCE", "Torvus", "Torvus@1407");

            PreparedStatement stmt = conn.prepareStatement("insert into StudentImages values (?, ?)");
            for(InputStream in : capturedImages){
                stmt.setLong(1, this.regNum);
                stmt.setBinaryStream(2, in);
                if(stmt.executeUpdate() == 0)
                    flag = false;
            }
            if(flag){
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Register status");
                success.setHeaderText("Registration Successful.");
                success.setContentText("Student : " + this.regNum + " registered.");
                success.showAndWait();
                capture.release();
                root = FXMLLoader.load(getClass().getResource("StudentLogin.fxml"));
                stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.getScene().setRoot(root);
                stage.setTitle("Student Login / Register - Auto Attendance System");
                stage.show();
            } else {
                throw new Exception("Image upload failed.");
            }
        } catch(Exception exception) {
            Alert failure = new Alert(Alert.AlertType.ERROR);
            failure.setTitle("Register status");
            failure.setHeaderText("Registration failed.");
            failure.setContentText(exception.getMessage());
            failure.showAndWait();
            exception.printStackTrace();
        }

    }

    class DaemonThread implements Runnable {
        protected volatile boolean runnable = false;

        @Override
        public void run() {
            synchronized (this) {
                while(runnable) {
                    if(capture.grab()){
                        try {
                            // Reading the image frame from webcam
                            capture.retrieve(frame);
                            faceDetection(frame);
                            // Encoding the image
                            Imgcodecs.imencode(".jpg", frame, mem);
                            // Storing the encoded Mat in a byte array
                            byte[] byteArray = mem.toArray();

                            // Displaying the image
                            InputStream in = new ByteArrayInputStream(byteArray);
                            BufferedImage image = ImageIO.read(in);
                            WritableImage writableImage = SwingFXUtils.toFXImage(image, null);
                            regImgView.setImage(writableImage);

                            if(capturedCount == 10) {
                                submitBtn.setDisable(false);
                                captureBtn.setDisable(true);
                            }

                            if(!runnable)
                                this.wait();

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    void faceDetection(Mat image) {
        // Build and load the cascade classifier
        CascadeClassifier classifier = new CascadeClassifier();
        if(!classifier.load("D:\\Image_Processing\\Auto_Attendence_Tracker\\src\\main\\resources\\haarcascade_frontalface_alt.xml"))
            System.err.println("Unable to load the classifier.");
        // Covert the image to grayscale and eqaulize the frame histogram to get better results
        Mat imageGrey = new Mat();
        Imgproc.cvtColor(image, imageGrey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(imageGrey, imageGrey);
        // Set minimum size of the face to detect - Here 20% of the frame size
        int absoluteFaceSize = 0;
        int height = imageGrey.rows();
        if(Math.round(height * 0.2f) > 0)
            absoluteFaceSize = Math.round(height * 0.2f);
        // Detection using detectMultiScale function detects objects of different sizes in the input image.
        // The detected objects are returned as a list of rectangles.
        MatOfRect faces = new MatOfRect();
        classifier.detectMultiScale(imageGrey,faces,1.1, 2, Objdetect.CASCADE_SCALE_IMAGE, new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        // Drawing Rectangles around detected faces
        Rect[] facesArray = faces.toArray();
        int i = 1;
        for(Rect face : facesArray) {
            // System.out.println(face); // face -> coordinates of face location
            // Send to recognizer
            faceCoord = face;
            Mat faceCrop = new Mat(image, face);
            i += 1;
            Imgproc.rectangle(image, new Point(face.x, face.y), new Point(face.x + face.width, face.y + face.height), new Scalar(255, 0, 0), 4);
        }

    }
}
