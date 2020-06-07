package approval.model;


/**
 *
 * This class stores all information about an request to approve a travel
 * refund request for a specific teacher.
 */
public class ApprovalRequest {

    private String teacher;
    private String student;
    private double costs;
   // private ApprovalTravelMode travelMode;
    private String id;


    public ApprovalRequest() {
        super();
        setStudent(null);
        setTeacher(null);
        setCosts(-1);
    }

    public ApprovalRequest(String teacher, String student, double costs/*, ApprovalTravelMode mode*/) {
        super();
        setStudent(teacher);
        setTeacher(student);
        setCosts(costs);
       // setTravelMode(mode);
    }


    public ApprovalRequest(String teacher, String student, double costs, String id) {
        this(teacher, student, costs);
        setId(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public double getCosts() {
        return costs;
    }

    public void setCosts(double costs) {
        this.costs = costs;
    }

    @Override
    public String toString() {
        return teacher + "-" + student + "-" + costs/* + "-" + travelMode*/;
    }

   /* public ApprovalTravelMode getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(ApprovalTravelMode travelMode) {
        this.travelMode = travelMode;
    }*/
}
