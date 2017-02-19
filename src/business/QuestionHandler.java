package business;

import model.Question;
import model.QuestionWeight;
import model.Role;
import model.SQLConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 9/9/15
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class QuestionHandler {
    public static Map<Integer, Question> questionMap = new HashMap<Integer, Question>(); //key: question id
    public static Map<Integer, List<QuestionWeight>> questionWeightsMap = new HashMap<Integer, List<QuestionWeight>>();

    public static void initializeQuestions(Integer unitId) throws SQLException {
        SQLConnector.getInstance();
        String queryStr = "select distinct que.id, que.questiontext\n" +
                "from mdl_quiz q\n" +
                "inner join mdl_course c on q.course = c.id and c.category = ?\n" +
                "inner join mdl_quiz_attempts qat on qat.quiz = q.id\n" +
                "inner join mdl_question_usages quba on qat.uniqueid = quba.id\n" +
                "LEFT JOIN mdl_question_attempts qa ON qa.questionusageid = quba.id\n" +
                "inner join mdl_question que on qa.questionid = que.id";
        PreparedStatement preparedStatement = SQLConnector.conn.prepareStatement(queryStr);
        preparedStatement.setInt(1, unitId);
        ResultSet questionSet = preparedStatement.executeQuery();
        while (questionSet.next()) {
            int questionId = questionSet.getInt(1);
            String questionText = questionSet.getString(2);
            questionText = questionText.replaceAll("&nbsp;", "");
            questionText = questionText.replaceAll("\\<.*?>", "");
            Question question = new Question(questionId, questionText);
            questionMap.put(questionId, question);
        }
    }

    public static void initializeQuestionWeights() {
        Integer questionId;
        List<QuestionWeight> questionWeights;

        questionId = 1;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 5));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 2;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 3));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 3;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 3));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 4;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 3));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 5;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 0));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 0));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 4));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 0));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 6;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 3));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 0));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 7;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 4));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 3));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 1));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 8;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 3));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 3));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 4));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 0));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 9;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 3));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 3));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 0));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 10;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 0));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 11;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 4));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 0));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 12;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 4));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 4));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 3));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 13;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 4));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 3));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 0));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 14;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 3));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 15;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 0));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 0));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 16;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 4));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 4));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 17;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 1));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 1));
        questionWeightsMap.put(questionId, questionWeights);

        questionId = 18;
        questionWeights = new ArrayList<QuestionWeight>();
        questionWeights.add(new QuestionWeight(questionId, Role.UNIT_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.CUSTOMER_MANAGER, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.SENIOR_EXPERT, 5));
        questionWeights.add(new QuestionWeight(questionId, Role.EXPERT, 4));
        questionWeights.add(new QuestionWeight(questionId, Role.SECRETARY, 3));
        questionWeightsMap.put(questionId, questionWeights);
    }

    public static double findQuestionWeight(Integer questionCode, Role role) {
        List<QuestionWeight> questionWeights = questionWeightsMap.get(questionCode);
        if (questionWeights == null) {
            return -1;
        }
        for (QuestionWeight questionWeight : questionWeights) {
            if (questionWeight.getAssessee() == role) {
                return questionWeight.getWeight();
            }
        }
        return -1;
    }
}
