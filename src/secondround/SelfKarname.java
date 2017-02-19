package secondround;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 10/21/16
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelfKarname implements Serializable{

    UserInfo userInfo;
    List<KPIScoreInfo> kpisScoreInfo;
    TotalScoreInfo totalScoreInfo;

    public SelfKarname(UserInfo userInfo, List<KPIScoreInfo> kpisScoreInfo, TotalScoreInfo totalScoreInfo) {
        this.userInfo = userInfo;
        this.kpisScoreInfo = kpisScoreInfo;
        this.totalScoreInfo = totalScoreInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public List<KPIScoreInfo> getKpisScoreInfo() {
        return kpisScoreInfo;
    }

    public TotalScoreInfo getTotalScoreInfo() {
        return totalScoreInfo;
    }
}

class UserInfo {
    private String firstName;
    private String lastName;
    private String userId;
    private String unit;
    private String section = "سایر";
    private String position;
    private String userName;

    public UserInfo(String firstName, String lastName, String userId, String unit, String section, String position, String userName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
        this.unit = unit;
        this.section = section;
        this.position = position;
        this.userName = userName;
    }

    String getFirstName() {
        return firstName;
    }

    String getLastName() {
        return lastName;
    }

    String getUserId() {
        return userId;
    }

    String getUnit() {
        return unit;
    }

    String getSection() {
        return section;
    }

    String getPosition() {
        return position;
    }

    public String getUserName() {
        return userName;
    }
}
class KPIScoreInfo {
    String index;
    String kpiName;
    String previousScore;
    String score;
    String scoreSelf;
    String averageScore;
    String kpiCode;

    KPIScoreInfo(String index, String kpiName, String previousScore, String score, String scoreSelf, String averageScore, String kpiCode) {
        this.index = index;
        this.kpiName = kpiName;
        this.previousScore = previousScore;
        this.score = score;
        this.scoreSelf = scoreSelf;
        this.averageScore = averageScore;
        this.kpiCode = kpiCode;
    }

    String getIndex() {
        return index;
    }

    String getKpiName() {
        return kpiName;
    }

    String getPreviousScore() {
        return previousScore;
    }

    String getScore() {
        return score;
    }

    String getScoreSelf() {
        return scoreSelf;
    }

    String getAverageScore() {
        return averageScore;
    }

    void setAverageScore(String averageScore) {
        this.averageScore = averageScore;
    }

    public String getKpiCode() {
        return kpiCode;
    }
}
class TotalScoreInfo {
    String totalScore;
    String totalSelfScore;
    String outlierCount;
    String incompleteFormsCount;

    TotalScoreInfo(String totalScore, String totalSelfScore, String outlierCount, String incompleteFormsCount) {
        this.totalScore = totalScore;
        this.totalSelfScore = totalSelfScore;
        this.outlierCount = outlierCount;
        this.incompleteFormsCount = incompleteFormsCount;
    }

    String getTotalScore() {
        return totalScore;
    }

    String getTotalSelfScore() {
        return totalSelfScore;
    }

    String getOutlierCount() {
        return outlierCount;
    }

    String getIncompleteFormsCount() {
        return incompleteFormsCount;
    }

}
