import java.io.Serializable;
import java.util.Date;

public class Data implements Serializable {
    Date time;
    long predictedRegNum;

    Data (long label) {
        this.time = new Date();
        this.predictedRegNum = label;
    }
}