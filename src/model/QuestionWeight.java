package model;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 9/2/15
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class QuestionWeight {
    private Integer questionCode;
    private Role assessee;
    private double weight;

    public QuestionWeight(Integer questionCode, Role assessee, double weight) {
        this.questionCode = questionCode;
        this.assessee = assessee;
        this.weight = weight;
    }

    public Role getAssessee() {
        return assessee;
    }

    public void setAssessee(Role assessee) {
        this.assessee = assessee;
    }

    public Integer getQuestionCode() {
        return questionCode;
    }

    public void setQuestionCode(Integer questionCode) {
        this.questionCode = questionCode;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
