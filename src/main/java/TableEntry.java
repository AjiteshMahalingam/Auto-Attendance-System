public class TableEntry {
    String  courseId;
    int staffId;
    int presentClasses;
    int totalClasses;
    TableEntry(String CourseId, int StaffId, int presentClasses, int totalClasses){
        this.courseId = CourseId;
        this.staffId = StaffId;
        this.presentClasses = presentClasses;
        this.totalClasses = totalClasses;
    }
    public String getCourseId() {
        return this.courseId;
    }
    public int getStaffId() {
        return this.staffId;
    }
    public int getPresentClasses(){
        return this.presentClasses;
    }
    public  int getTotalClasses() {
        return this.totalClasses;
    }
}
