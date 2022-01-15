import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

public class WebCaptureController {
    private VideoCapture capture;
    private Mat frame = new Mat();
    private MatOfByte mem = new MatOfByte();
    private DaemonThread thread;

    @FXML
    private Button startBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private ImageView imgView;

    public void startCapture(ActionEvent e) {
        capture = new VideoCapture(0);
        thread = new DaemonThread();

        Thread t = new Thread(thread);
        t.setDaemon(true);
        thread.runnable = true;
        t.start();
        startBtn.setDisable(true);
        stopBtn.setDisable(false);
    }

    public void stopCapture(ActionEvent e) {
        thread.runnable = false;
        startBtn.setDisable(false);
        stopBtn.setDisable(true);
        capture.release();
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
            Mat faceCrop = new Mat(image, face);
            i += 1;
            Imgproc.rectangle(image, new Point(face.x, face.y), new Point(face.x + face.width, face.y + face.height), new Scalar(255, 0, 0), 4);
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
                            imgView.setImage(writableImage);

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

}
