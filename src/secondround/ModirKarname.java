package secondround;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 10/21/16
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModirKarname implements Serializable{
    private SelfKarname selfKarname;
    private Map<String, Map<String, BigDecimal>> kpiMianginVazni_step;
    private Map<String, BigDecimal> totalScore_step;
    private Map<String, String> categoryAverage;

    public ModirKarname(SelfKarname selfKarname, Map<String, Map<String, BigDecimal>> kpiMianginVazni_step, Map<String, BigDecimal> totalScore_step) {
        this.selfKarname = selfKarname;
        this.kpiMianginVazni_step = kpiMianginVazni_step;
        this.totalScore_step = totalScore_step;
        categoryAverage = new HashMap<String, String>();
        correctScoreValues();
    }

    private void correctScoreValues() {
        for (String key : kpiMianginVazni_step.keySet()) {
            Map<String, BigDecimal> mianginVazni_step = kpiMianginVazni_step.get(key);
            for (String innerKey : mianginVazni_step.keySet()) {
                mianginVazni_step.put(innerKey, mianginVazni_step.get(innerKey).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_CEILING));
            }
        }

        for (String key : totalScore_step.keySet()) {
            totalScore_step.put(key, totalScore_step.get(key).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_CEILING));
        }

        for (String category : MainExecutor.allCategories) {
            categoryAverage.put(category, "-");
        }
    }

    public SelfKarname getSelfKarname() {
        return selfKarname;
    }

    public Map<String, Map<String, BigDecimal>> getKpiMianginVazni_step() {
        return kpiMianginVazni_step;
    }

    public Map<String, BigDecimal> getTotalScore_step() {
        return totalScore_step;
    }

    public Map<String, String> getCategoryAverage() {
        return categoryAverage;
    }

    public void setCategoryAverage(Map<String, String> categoryAverage) {
        this.categoryAverage = categoryAverage;
    }
}
