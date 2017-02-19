package business;

import model.AssessmentScore;
import model.Person;
import model.QuestionResult;
import model.Role;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 9/9/15
 * Time: 11:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataAnalyzer {
    public static void analyzeAssessmentData(Map<String, Map<Integer, QuestionResult>> allUnitQuestionReults, Role role, String kpi) throws IOException {
        Map<String, Map<Integer, BigDecimal>> finalResults = new HashMap<String, Map<Integer, BigDecimal>>();
        for (String assesseeName : allUnitQuestionReults.keySet()) {
            Role assesseeRole = Runner.findRole(Runner.UNIT_NAME, assesseeName);
            Person assessee = new Person(assesseeName, assesseeRole);
            Map<Integer, QuestionResult> questionResults = allUnitQuestionReults.get(assesseeName);
            Map<Integer, BigDecimal> perPersonFinalResults = new HashMap<Integer, BigDecimal>();
            for (Integer questionId : questionResults.keySet()) {
                QuestionResult questionResult = questionResults.get(questionId);
                double questionWeight = QuestionHandler.findQuestionWeight(questionResult.getQuestionCode(), assessee.getRole());
                double sumScore = 0;
                double weightedSumScore;
                double maxScore = 0;
                double weightedMaxScore;
                for (AssessmentScore score : questionResult.getSocres()) {
                    double roleWeight = RoleWeightHandler.findWeight(score.getAssessor().getRole(), assessee.getRole());
                    sumScore += (score.getScore() * roleWeight);
                    maxScore += roleWeight;
                }
                weightedSumScore = sumScore * questionWeight;
                weightedMaxScore = maxScore * questionWeight;
                double result = weightedMaxScore != 0 ? weightedSumScore / weightedMaxScore : 0;
                BigDecimal roundedResult = new BigDecimal(result).setScale(3, RoundingMode.FLOOR);
                perPersonFinalResults.put(questionId, roundedResult);
            }
            finalResults.put(assesseeName, perPersonFinalResults);
        }
        makeArray(finalResults, role, kpi);
    }

    private static void makeArray(Map<String, Map<Integer, BigDecimal>> finalResults, Role role, String kpi) throws IOException {
        ArrayList<Integer> allQuestions = new ArrayList<Integer>();
        ArrayList<String> allPersons = new ArrayList<String>();
        for (String person : finalResults.keySet()) {
            allPersons.add(person);
            Map<Integer, BigDecimal> questions = finalResults.get(person);
            for (Integer questionId : questions.keySet()) {
                if (!allQuestions.contains(questionId)) {
                    allQuestions.add(questionId);
                }
            }
        }
        Object[][] output = new Object[allQuestions.size() + 6][allPersons.size() + 7];
        output[0][0] = "";
        output[0][1] = "";
        for (int i = 0; i < allQuestions.size(); i++) {
            output[i + 1][0] = allQuestions.get(i);
            output[i + 1][1] = QuestionHandler.questionMap.get(allQuestions.get(i)).getText();
        }
        output[allQuestions.size() + 2][1] = "میانگین";
        output[allQuestions.size() + 3][1] = "کمینه";
        output[allQuestions.size() + 4][1] = "بیشینه";

        for (int i = 0; i < allPersons.size(); i++) {
            output[0][i + 2] = allPersons.get(i);
        }
        output[0][allPersons.size() + 3] = "میانگین";
        output[0][allPersons.size() + 4] = "کمینه";
        output[0][allPersons.size() + 5] = "بیشینه";

        for (int i = 0; i < allQuestions.size(); i++) {
            BigDecimal sum = BigDecimal.ZERO;
            BigDecimal min = new BigDecimal(Integer.MAX_VALUE);
            BigDecimal max = BigDecimal.ZERO;
            int count = 0;
            for (int j = 0; j < allPersons.size(); j++) {
                Map<Integer, BigDecimal> questionResults = finalResults.get(output[0][j + 2]);
                BigDecimal score = questionResults.get(output[i + 1][0]);
                output[i + 1][j + 2] = (score != null && score.compareTo(BigDecimal.ZERO) != 0) ? score : "";
                if (score != null && score.compareTo(BigDecimal.ZERO) != 0) {
                    count++;
                    sum = sum.add(score);
                    if (score.compareTo(min) < 0) {
                        min = score;
                    }
                    if (score.compareTo(max) > 0) {
                        max = score;
                    }
                }
            }
            if (count != 0) {
                output[i + 1][allPersons.size() + 3] = sum.divide(new BigDecimal(count), 3, RoundingMode.FLOOR);
                output[i + 1][allPersons.size() + 4] = min;
                output[i + 1][allPersons.size() + 5] = max;
            }
        }
        for (int j = 0; j < allPersons.size(); j++) {
            BigDecimal sum = BigDecimal.ZERO;
            BigDecimal min = new BigDecimal(Integer.MAX_VALUE);
            BigDecimal max = BigDecimal.ZERO;
            int count = 0;
            for (int i = 0; i < allQuestions.size(); i++) {
                String cellObj = output[i + 1][j + 2].toString();
                if (!cellObj.equals("")) {
                    count++;
                    BigDecimal cellVal = new BigDecimal(cellObj);
                    sum = sum.add(cellVal);
                    if (cellVal.compareTo(min) < 0) {
                        min = cellVal;
                    }
                    if (cellVal.compareTo(max) > 0) {
                        max = cellVal;
                    }
                }
            }
            if (count != 0) {
                output[allQuestions.size() + 2][j + 2] = sum.divide(new BigDecimal(count), 3, RoundingMode.FLOOR);
                output[allQuestions.size() + 3][j + 2] = min;
                output[allQuestions.size() + 4][j + 2] = max;
            }
        }
        writeToExcelFile(output, role, kpi);
    }

    private static void writeToExcelFile(Object[][] output, Role role, String kpi) throws IOException {
        String fileName;
        String suffix = ".xls";
        fileName = "resources/analysis/" + Runner.UNIT_NAME;
        if (role != null) {
            fileName = fileName + "_" + role;
        }
        if (kpi != null) {
            fileName = fileName + "_" + kpi;
        }
        File file = new File(fileName + suffix);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out = new FileOutputStream(file);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        sheet.setRightToLeft(true);

        HSSFFont questionFont = workbook.createFont();
        questionFont.setFontName("B Nazanin");
        questionFont.setColor(IndexedColors.BLACK.getIndex());
        questionFont.setFontHeightInPoints((short) 10);
        CellStyle questionStyle = workbook.createCellStyle();
        questionStyle.setFont(questionFont);
        questionStyle.setAlignment(CellStyle.ALIGN_CENTER);
        for (int i = 0; i < output.length; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < output[i].length; j++) {
                Cell cell = row.createCell(j);
                Object cellVal = output[i][j];
                if (cellVal instanceof String) {
                    cell.setCellValue((String) cellVal);
                } else if (cellVal instanceof Integer) {
                    cell.setCellValue((Integer) cellVal);
                } else if (cellVal instanceof BigDecimal) {
                    cell.setCellValue(((BigDecimal) cellVal).doubleValue());
                }
                cell.setCellStyle(questionStyle);
                if (j != 1) {
                    sheet.setColumnWidth(j, 4000);
                } else {
                    sheet.setColumnWidth(j, 6000);
                }
            }
        }
        workbook.write(out);
        out.close();
    }
}
