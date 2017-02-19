package model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 9/2/15
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class QuestionResult {
    private Integer questionCode;
    private List<AssessmentScore> socres;

    public QuestionResult() {
    }

    public QuestionResult(Integer questionCode, List<AssessmentScore> socres) {
        this.questionCode = questionCode;
        this.socres = socres;
    }

    public Integer getQuestionCode() {
        return questionCode;
    }

    public void setQuestionCode(Integer questionCode) {
        this.questionCode = questionCode;
    }

    public List<AssessmentScore> getSocres() {
        return socres;
    }

    public void setSocres(List<AssessmentScore> socres) {
        this.socres = socres;
    }
}
