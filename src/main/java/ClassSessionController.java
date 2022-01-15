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
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.security.spec.ECField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static org.opencv.imgcodecs.Imgcodecs.IMREAD_UNCHANGED;

public class ClassSessionController implements Initializable {
    private Parent root;
    private Scene scene;
    private Stage stage;
    private VideoCapture capture;
    private Mat frame = new Mat();
    private MatOfByte mem = new MatOfByte();
    private DaemonThread thread;
    LBPHFaceRecognizer model;
    private long regNum;
    private long predictedLabel = 0;
    private Thread sendThread;
    private Thread readThread;
    private int staffId;
    private String courseId;

    int port;
    Socket socket;
    ObjectOutputStream output;
    ObjectInputStream input;
    boolean leave = false;

    @FXML
    private ImageView camImgView;
    @FXML
    private Label courseLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Button leaveBtn;
    @FXML
    private Label staffLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.output.writeObject(new Data(this.regNum));

            this.courseLabel.setText(courseId);
            this.staffLabel.setText(String.valueOf(staffId));
            this.dateLabel.setText(new Date().toString());

            capture = new VideoCapture(0);
            thread = new DaemonThread();
            Thread t = new Thread(thread);
            t.setDaemon(true);
            thread.runnable = true;
            t.start();
            ArrayList<Mat> images = new ArrayList<>();
            ArrayList<Long> labels = new ArrayList<>();

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/AUTO_ATTENDANCE", "Torvus", "Torvus@1407");
            PreparedStatement stmt = con.prepareStatement("select * from StudentImages");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                InputStream in = rs.getBinaryStream(2);
                Long regNum = rs.getLong(1);
                int nRead;
                byte[] data = new byte[16 * 1024];
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                while ((nRead = in.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                byte[] bytes = buffer.toByteArray();
                Mat mat = Imgcodecs.imdecode(new MatOfByte(bytes), IMREAD_UNCHANGED);
                Mat grey = new Mat();
                Imgproc.cvtColor(mat, grey, Imgproc.COLOR_RGB2GRAY);
                images.add(grey);
                labels.add(regNum);
            }
            int n = labels.size();
            Mat labelsMat = new Mat(n, 1, CvType.CV_32SC1);
            for(int i = 0; i < n; i++)
                labelsMat.put(i, 0, labels.get(i));
            model = LBPHFaceRecognizer.create();
            model.train(images, labelsMat);

            Runnable send = new sendData();
            sendThread = new Thread(send);
            sendThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void switchHome (ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentHome.fxml"));
            StudentHomeController homeController = new StudentHomeController(this.regNum);
            loader.setController(homeController);
            root = loader.load();
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Student Home - Auto Attendance System");
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void leaveClass(ActionEvent e) {
        try {
            output.writeObject(new Data(-1));
            input = new ObjectInputStream(socket.getInputStream());
            Data inpData = (Data) input.readObject();
            if(inpData.predictedRegNum == -1) {
                System.out.println("Left class");
                socket.close();
            }
            capture.release();
            switchHome(e);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    ClassSessionController (long regNum, int StaffId, String courseId, Socket socket){
        this.regNum = regNum;
        this.staffId = StaffId;
        this.courseId = courseId;
        this.port = Integer.parseInt((staffId % 100) + courseId.substring(3));
        this.socket = socket;
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
                            camImgView.setImage(writableImage);

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

    class sendData implements Runnable {
        @Override
        public void run(){
            try {
                while(true){
                    TimeUnit.SECONDS.sleep(1);
                    output.writeObject(new Data(predictedLabel));
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        predictedLabel = 0;
        for(Rect face : facesArray) {
            // System.out.println(face); // face -> coordinates of face location
            // Send to recognizer
            Mat faceCrop = new Mat(imageGrey, face);
            predictedLabel = model.predict_label(faceCrop);
            if(model.predict_label(faceCrop) == this.regNum)
                predictedLabel = this.regNum;
            i += 1;
            Imgproc.rectangle(image, new Point(face.x, face.y), new Point(face.x + face.width, face.y + face.height), new Scalar(255, 0, 0), 4);
            Imgproc.putText(image, String.valueOf(predictedLabel), new Point(face.x, face.y) , Imgproc.FONT_HERSHEY_COMPLEX, 1, new Scalar(0, 0, 0), 4);
        }
    }
}
