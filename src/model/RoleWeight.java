package model;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 9/2/15
 * Time: 10:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class RoleWeight {
    private Role assessee;
    private Role assessor;
    private double weight;

    public RoleWeight(Role assessee, Role assessor, double weight) {
        this.assessee = assessee;
        this.assessor = assessor;
        this.weight = weight;
    }

    public Role getAssessor() {
        return assessor;
    }

    public void setAssessor(Role assessor) {
        this.assessor = assessor;
    }

    public Role getAssessee() {
        return assessee;
    }

    public void setAssessee(Role assessee) {
        this.assessee = assessee;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
