package model;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 9/2/15
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class AssessmentScore {
    private Person assessor;
    private double score;

    public Person getAssessor() {
        return assessor;
    }

    public void setAssessor(Person assessor) {
        this.assessor = assessor;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
