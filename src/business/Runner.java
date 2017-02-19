package business;

import com.mysql.jdbc.Statement;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import model.*;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 9/2/15
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class Runner {
    public static ArrayList<ArrayList<String>> rawExtractedData;
    public static ArrayList<QuestionResult> questionResults = new ArrayList<QuestionResult>();
    //    private static String filePath = "resources/wight for assessment-940531-v01.xls";
    public static final Integer UNIT_ID = 25;
    public static final String UNIT_NAME = "CRM";

    public static void main(String[] args) throws IOException, SQLException {
//        phase2QuestionsInCourses();
//        phase2CourseUpdate();
//        phase2QuizQuestionAdd();
//        phase2QuestionInsertion();
//        phase2UpdateExceptionCourseSectionName();
//        phase2UpdateCourseSectionName();
//        phase2InsertQuizforAllCourses(true);
//        Connection conn = SQLConnector.conn;
//        phase2QuizInsertion(828, "FinalTest2", conn);

//        phase2Enrollment();
//        createCourses(27);
//        createQuiz(828);


//        fetchDataFromDB(8);
        getQuestionTexts();
//        enrolExtraUsers("1.csv");
//        enrolSupportUsers();
//        insertSupportQuestions();
//        RoleWeightHandler.initializeRoleWeights();
//        QuestionHandler.initializeQuestions(UNIT_ID);
//        QuestionHandler.initializeQuestionWeights();
//        analyzeData();
    }

    private static void phase2QuestionsInCourses() throws SQLException, IOException {
        SQLConnector.getInstance();
/*
        String queryStr = "select c.idnumber, que.id, que.questiontext\n" +
                "from mdl_quiz q\n" +
                "inner join mdl_course c on q.course = c.id and c.idnumber like '2*%' and q.name like '%خود%'\n" +
                "inner join mdl_quiz_attempts qat on qat.quiz = q.id\n" +
                "inner join mdl_question_usages quba on qat.uniqueid = quba.id\n" +
                "LEFT JOIN mdl_question_attempts qa ON qa.questionusageid = quba.id\n" +
                "inner join mdl_question que on qa.questionid = que.id\n" +
                "order by c.idnumber";
*/
        String queryStr = "select c.idnumber, qu.id, qu.questiontext from mdl_course c\n" +
                "inner join mdl_quiz q on c.id = q.course and c.idnumber like '2*%' and q.name like '%خود%'\n" +
                "inner join mdl_quiz_slots qs on qs.quizid = q.id\n" +
                "inner join mdl_question qu on qs.questionid = qu.id";
        PreparedStatement preparedStatement = SQLConnector.conn.prepareStatement(queryStr);
        ResultSet questionSet = preparedStatement.executeQuery();
        File fileDir = new File("courseQuestions.csv");
        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileDir), "UTF8"));
        while (questionSet.next()) {
            String courseId = questionSet.getString(1);
            int questionId = questionSet.getInt(2);
            String questionIdStr = questionId + "";
            String questionText = questionSet.getString(3);
            questionText = questionText.replaceAll("&nbsp;", "");
            questionText = questionText.replaceAll("\\<.*?>", "");
            out.append(courseId).append("\t").append(questionIdStr).append("\t").append(questionText).append("\r\n");
        }
    }

    private static void phase2CourseUpdate() throws SQLException {
        Connection conn = SQLConnector.conn;
        conn.setAutoCommit(false);
        String courseSelectionStr = "select id from mdl_course where idnumber like '2*%'";
        PreparedStatement statement = conn.prepareStatement(courseSelectionStr);
        ResultSet allCourses = statement.executeQuery();
        String time = System.currentTimeMillis() / 1000 + "";
        while (allCourses.next()) {
            long courseId = allCourses.getLong(1);
            String updateStr = "update mdl_course_sections cs\n" +
                    "inner join mdl_course c on cs.course = c.id\n" +
                    "set cs.visible = 0 where cs.section = 1 and c.id = ?";
            PreparedStatement statement1 = conn.prepareStatement(updateStr);
            statement1.setLong(1, courseId);
            statement1.executeUpdate();
            String courseCacheUpdateStr = "UPDATE mdl_course SET cacherev = (CASE WHEN cacherev IS NULL THEN " + Integer.valueOf(time) + " WHEN cacherev < " + Integer.valueOf(time) + " THEN " + Integer.valueOf(time) + " WHEN cacherev > " + Integer.valueOf(time) + " + 3600 THEN " + Integer.valueOf(time) + " ELSE cacherev + 1 END) WHERE id = '" + courseId + "'";
            executeUpdate(conn, courseCacheUpdateStr);
        }
    }

    private static void phase2QuestionInsertion() throws SQLException {
        Connection conn = SQLConnector.conn;
        //boroon: 855, daftar: 856
        long boroon = 855;
        long daftar = 856;
//        String selectBoroonQuizStr = "select q.id from mdl_quiz q\n" +
//                "inner join mdl_course c on q.course = c.id and c.idnumber like '2*%' and q.name like '%برون%'";
        String selectDaftarQuizStr = "select q.id from mdl_quiz q\n" +
                "inner join mdl_course c on q.course = c.id and c.idnumber like '2*%' and q.name like '%دفتر%'";
//        PreparedStatement quizStatement = conn.prepareStatement(selectBoroonQuizStr);
        PreparedStatement quizStatement = conn.prepareStatement(selectDaftarQuizStr);
        ResultSet quizResultSet = quizStatement.executeQuery();
//        String selectBoroonQuestionStr = "select questionid from mdl_quiz_slots where quizid = " + boroon;
        String selectDaftarQuestionStr = "select questionid from mdl_quiz_slots where quizid = " + daftar;
//        PreparedStatement statement = conn.prepareStatement(selectBoroonQuestionStr);
        PreparedStatement statement = conn.prepareStatement(selectDaftarQuestionStr);
        conn.setAutoCommit(false);
        try {
            while (quizResultSet.next()) {
                long quizId = quizResultSet.getLong(1);
                ResultSet questionResultSet = statement.executeQuery();
                while (questionResultSet.next()) {
                    long questionId = questionResultSet.getLong(1);
                    ResultSet existingQuizSlotSet = conn.prepareStatement("select id from mdl_quiz_slots where quizid = " + quizId + " and questionid = " + questionId)
                            .executeQuery();
                    if (!existingQuizSlotSet.next()) {
                        insertSingleQuiz(quizId, questionId, conn);
                    } else {
                        System.out.println("Quiz " + quizId + " Question " + questionId + " Already Exists");
                    }
                }
                System.out.println("questions inserted for quiz: " + quizId);
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
        }
    }

    private static void phase2QuizQuestionAdd() throws IOException, SQLException {
        ArrayList<Integer> questionIds;
        RandomAccessFile enrollmentFile = new RandomAccessFile("data/Quiz.csv", "rw");
        String line;
        SQLConnector.getInstance();
        Connection conn = SQLConnector.conn;
        conn.setAutoCommit(false);
        try {
            while ((line = enrollmentFile.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                String referenceCourseIdNumber = tokenizer.nextToken();
                String selfQuestionIdsQueryStr = "select qu.id from mdl_question qu\n" +
                        "inner join mdl_quiz_slots qs on qu.id = qs.questionid\n" +
                        "inner join mdl_quiz q on qs.quizid = q.id\n" +
                        "inner join mdl_course c on q.course = c.id\n" +
                        "where c.idnumber = ? and q.name like '%خود%'";
                PreparedStatement selfQuestionIdsStmt = conn.prepareStatement(selfQuestionIdsQueryStr);
                selfQuestionIdsStmt.setString(1, referenceCourseIdNumber);
                ResultSet selfQuestionIdsResultSet = selfQuestionIdsStmt.executeQuery();
                questionIds = new ArrayList<Integer>();
                while (selfQuestionIdsResultSet.next()) {
                    int questionId = selfQuestionIdsResultSet.getInt(1);
                    questionIds.add(questionId);
                }
                while (tokenizer.hasMoreTokens()) {
                    String nextCourseIdNumber = tokenizer.nextToken();
                    String quizFinderQueryStr = "select q.id from mdl_quiz q inner join mdl_course c on q.course = c.id\n" +
                            "where q.name like '%خود%' and c.idnumber = ?";
                    PreparedStatement quizFinderStmt = conn.prepareStatement(quizFinderQueryStr);
                    quizFinderStmt.setString(1, nextCourseIdNumber);
                    ResultSet courseFinderResultSet = quizFinderStmt.executeQuery();
                    courseFinderResultSet.next();
                    int quizId = 0;
                    try {
                        quizId = courseFinderResultSet.getInt(1);
                    } catch (SQLException e) {
                        System.out.println("%%%%%%%%%%%% " + nextCourseIdNumber);
                    }
                    for (int questionId : questionIds) {
                        insertSingleQuiz(quizId, questionId, conn);
                    }
                    System.out.println("Questions Inserted for course: " + nextCourseIdNumber);
                }
            }
            conn.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
        }
    }

    private static void insertSingleQuiz(long quizId, long questionId, Connection conn) throws SQLException {
        String slotStr = "select max(slot) from mdl_quiz_slots where quizid = " + quizId;
        PreparedStatement slotStatement = conn.prepareStatement(slotStr);
        ResultSet resultSet = slotStatement.executeQuery();
        long slot;
        if (resultSet.next()) {
            slot = resultSet.getInt(1) + 1;
        } else {
            slot = 1;
        }

        String insertStr = "INSERT INTO mdl_quiz_slots (quizid,questionid,maxmark,slot,page) VALUES(?, ?, '1.0000000', ?, '1')";
        PreparedStatement statement = conn.prepareStatement(insertStr);
        statement.setLong(1, quizId);
        statement.setLong(2, questionId);
        statement.setLong(3, slot);
        statement.executeUpdate();
    }

    private static void phase2UpdateExceptionCourseSectionName() throws SQLException {
        Connection conn = SQLConnector.conn;
        List<UserNameDS> names = new ArrayList<UserNameDS>();
        String time = System.currentTimeMillis() / 1000 + "";
        String prefix = "نظرسنجی در خصوص عملکرد ";

        UserNameDS und1 = new UserNameDS(697, "Mahdi Ramezani", "مهدی رمضانی");
        names.add(und1);

        UserNameDS und2 = new UserNameDS(736, "MohammadAli Fardad", "محمدعلی فرداد");
        names.add(und2);

        UserNameDS und3 = new UserNameDS(516, "Mahdi Torki", "مهدی ترکی");
        names.add(und3);

        UserNameDS und4 = new UserNameDS(831, "Hamed Hosseininejad*2", "حامد حسینی‌نژاد");
        names.add(und4);

        UserNameDS und5 = new UserNameDS(669, "Fatemeh Naeimi", "فاطمه نعیمی");
        names.add(und5);

        UserNameDS und6 = new UserNameDS(592, "Ali Reza Atai", "علیرضا عطائی");
        names.add(und6);

        UserNameDS und7 = new UserNameDS(716, "Masoumeh Zamani", "معصومه زمانی");
        names.add(und7);

        UserNameDS und8 = new UserNameDS(687, "Hossein Basreei", "حسین بصره‌ئی");
        names.add(und8);

        UserNameDS und9 = new UserNameDS(762, "Niloofar Roghani", "نیلوفر روغنی");
        names.add(und9);

        UserNameDS und10 = new UserNameDS(645, "Davoud Zaheri", "داوود ظاهری");
        names.add(und10);

        UserNameDS und11 = new UserNameDS(724, "Mina Fallahzadeh", "مینا فلاح‌زاده");
        names.add(und11);

        UserNameDS und12 = new UserNameDS(696, "Mahdi Kharazi", "مهدی خرازی");
        names.add(und12);

        UserNameDS und13 = new UserNameDS(541, " Shabnam Maleki", "شبنم ملکی");
        names.add(und13);

        UserNameDS und14 = new UserNameDS(773, "Rahele Morovati", "راحله مروتی");
        names.add(und14);

        UserNameDS und15 = new UserNameDS(732, "Mohamadreza Mirunesie haghi", "محمدرضا میریونسی");
        names.add(und15);

        UserNameDS und16 = new UserNameDS(577, "Amirabbas Kamali Pour", "امیرعباس کمالی‌پور");
        names.add(und16);

        UserNameDS und17 = new UserNameDS(480, "Haniyeh Khalaji", "هانیه خلجی");
        names.add(und17);

        UserNameDS und18 = new UserNameDS(735, "Mohammad Sabouri", "محمد صبوری");
        names.add(und18);

        UserNameDS und19 = new UserNameDS(555, "Mohamadreza Motevalian", "محمدرضا متولیان");
        names.add(und19);

        UserNameDS und20 = new UserNameDS(634, "Atiye moosavi", "عطیه موسوی");
        names.add(und20);

        UserNameDS und21 = new UserNameDS(588, "Saeid dabaghi", "سعید دباغی");
        names.add(und21);

        UserNameDS und22 = new UserNameDS(712, "Marziyeh Gerami", "مرضیه گرامی");
        names.add(und22);

        UserNameDS und23 = new UserNameDS(526, "Azam Mahdi Zadeh", "اعظم مهدی‌زاده");
        names.add(und23);

        UserNameDS und24 = new UserNameDS(832, "Naser Ahmadian*2", "ناصر احمدیان");
        names.add(und24);

        UserNameDS und25 = new UserNameDS(530, "Hoora Kiani", "هورا کیانی");
        names.add(und25);

        UserNameDS und26 = new UserNameDS(575, "Ali Reza Azimi", "علیرضا عظیمی");
        names.add(und26);

        UserNameDS und27 = new UserNameDS(612, "Abbas Hosseini", "عباس حسینی");
        names.add(und27);

        UserNameDS und28 = new UserNameDS(780, "Saeid Shirazian", "سعید شیرازیان");
        names.add(und28);

        UserNameDS und29 = new UserNameDS(623, "Ali Reza Namazi", "علیرضا نمازی");
        names.add(und29);

        UserNameDS und30 = new UserNameDS(512, "Masoumeh Davari", "معصومه داوری");
        names.add(und30);

        UserNameDS und31 = new UserNameDS(513, "Masoumeh Davari*2", "معصومه داوری");
        names.add(und31);

        UserNameDS und32 = new UserNameDS(748, "morteza moosavi", "مرتضی موسوی");
        names.add(und32);

        UserNameDS und33 = new UserNameDS(722, "Meysam Khanderoo", "میثم خنده رو");
        names.add(und33);

        UserNameDS und34 = new UserNameDS(574, "Abbas Afshari", "عباس افشاری");
        names.add(und34);

        UserNameDS und35 = new UserNameDS(602, "Fatemeh Ahmadi Fakhr", "فاطمه احمدی فخر");
        names.add(und35);

        UserNameDS und36 = new UserNameDS(730, "MoahamdHossein Jamali", "محمدحسین جمالی");
        names.add(und36);

        UserNameDS und37 = new UserNameDS(714, "Marziyeh yazdanipour", "مرضیه یزدانی‌پور");
        names.add(und37);

        UserNameDS und38 = new UserNameDS(825, "Zohre Saffarian", "زهره صفاریان");
        names.add(und38);

        UserNameDS und39 = new UserNameDS(693, "Keivan moosavi", "کیوان موسوی");
        names.add(und39);

        UserNameDS und40 = new UserNameDS(564, "Neda Rasuli", "ندا رسولی");
        names.add(und40);

        UserNameDS und41 = new UserNameDS(549, "Hamid Sadeghi", "حمید صادقی");
        names.add(und41);

        UserNameDS und42 = new UserNameDS(475, "Shapour Dadashloo*2", "شاهپور داداشلو");
        names.add(und42);

        UserNameDS und43 = new UserNameDS(824, "armin kian", "آرمین کیان");
        names.add(und43);

        UserNameDS und44 = new UserNameDS(834, "Mohammad hossein Shirzadi", "محمدحسین شیرزادی");
        names.add(und44);

        UserNameDS und45 = new UserNameDS(695, "Mahdi Babaei", "مهدی بابائی");
        names.add(und45);


        for (UserNameDS name : names) {
            String updateStr = "update mdl_course_sections cs\n" +
                    "inner join mdl_course c on cs.course = c.id and cs.section = 0 and c.fullname = ?\n" +
                    "set cs.name = ?";
            PreparedStatement statement = conn.prepareStatement(updateStr);
            statement.setString(1, name.getEnglishName());
            statement.setString(2, prefix + name.getPersianName());
            statement.executeUpdate();

            long courseId = name.getCourseId();
            String courseCacheUpdateStr = "UPDATE mdl_course SET cacherev = (CASE WHEN cacherev IS NULL THEN " + Integer.valueOf(time) + " WHEN cacherev < " + Integer.valueOf(time) + " THEN " + Integer.valueOf(time) + " WHEN cacherev > " + Integer.valueOf(time) + " + 3600 THEN " + Integer.valueOf(time) + " ELSE cacherev + 1 END) WHERE id = '" + courseId + "'";
            executeUpdate(conn, courseCacheUpdateStr);
        }
    }

    private static void phase2UpdateCourseSectionName() throws SQLException {
        Connection conn = SQLConnector.conn;
        String time = System.currentTimeMillis() / 1000 + "";

        String selectStr = "select c.id, concat(u.icq, ' ', u.skype) from mdl_course c\n" +
                "inner join mdl_user u on c.shortname = concat(u.firstname, ' ', u.lastname)\n" +
                "where c.idnumber like '2*%'";

        PreparedStatement statement = conn.prepareStatement(selectStr);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            long courseId = resultSet.getLong(1);
            String fullName = resultSet.getString(2);
            if (!fullName.equals(" ")) {
                String prefix = "نظرسنجی در خصوص عملکرد ";
                String sectionName = prefix + fullName;
                String updateStr = "update mdl_course_sections set name = ? where course = ? and section = 0";
                PreparedStatement updateStmt = conn.prepareStatement(updateStr);
                updateStmt.setString(1, sectionName);
                updateStmt.setLong(2, courseId);
                updateStmt.executeUpdate();

                String courseCacheUpdateStr = "UPDATE mdl_course SET cacherev = (CASE WHEN cacherev IS NULL THEN " + Integer.valueOf(time) + " WHEN cacherev < " + Integer.valueOf(time) + " THEN " + Integer.valueOf(time) + " WHEN cacherev > " + Integer.valueOf(time) + " + 3600 THEN " + Integer.valueOf(time) + " ELSE cacherev + 1 END) WHERE id = '" + courseId + "'";
                executeUpdate(conn, courseCacheUpdateStr);

                String updateSummaryStr = "update mdl_course_sections set summary = '<p style=\"text-align: right; direction: rtl;\">اطلاعات فرم‌ های نظرسنجی کاملا محرمانه خواهد ماند و صرفا در اختیار واحد سرمایه های انسانی قرار خواهد گرفت</p>' where course = " + courseId;
                PreparedStatement updateSummaryStmt = conn.prepareStatement(updateSummaryStr);
                updateSummaryStmt.executeUpdate();

                executeUpdate(conn, courseCacheUpdateStr);

                System.out.println(sectionName);
            }
        }
    }

    private static void phase2InsertQuizforAllCourses(boolean deploy) throws SQLException {
        String selectAllCoursesQueryStr = "select id from mdl_course where idnumber like '2*%'";
        Connection conn = SQLConnector.conn;
        PreparedStatement statement = conn.prepareStatement(selectAllCoursesQueryStr);
        ResultSet allPhase2Courses = statement.executeQuery();
        String daftarQuizName = "تکمیل فرم نظرسنجی توسط مسئول دفتر";
        String externalQuizName = "تکمیل فرم نظرسنجی توسط همکاران برون بخشی";
        String internalQuizName = "تکمیل فرم نظرسنجی توسط خود فرد(خودارزیابی)، مدیر و همکاران درون بخشی";

        conn.setAutoCommit(false);
        try {
            while (allPhase2Courses.next()) {
                int courseId = allPhase2Courses.getInt(1);
                phase2QuizInsertion(courseId, daftarQuizName, conn);
                phase2QuizInsertion(courseId, externalQuizName, conn);
                phase2QuizInsertion(courseId, internalQuizName, conn);
                System.out.println(courseId + " all quizzes inserted for course");
            }
            conn.commit();
            if (!deploy) {
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
        }
    }

    private static void phase2QuizInsertion(int courseId, String quizName, Connection conn) throws SQLException {
        String time = "1472741373@";
        String quizId = "429@";
        String courseModuleId = "1000@";
        String contextId = "5829@";
        String contextPath = "/1/608/5813/5814/";
        String quizFeedbackId = "470@";
        String gradeItemId = "884@";
        String sequence = "995,997,999,1000@@";
        String sortOrder = "4@";
        String section;
        String depth;
        String gradeCategory;

        time = System.currentTimeMillis() / 1000 + "";
        String courseModuleInsertionStr = "INSERT INTO mdl_course_modules (course,module,instance,visible,visibleold,idnumber,groupmode,groupingid,completion,completiongradeitemnumber,completionview,completionexpected,availability,showdescription,added) VALUES('" + courseId + "','16','0','1','1','','0','0','1',NULL,'0','0',NULL,'0','" + time + "')";
        String courseModuleInsertionResult = executeUpdate(conn, courseModuleInsertionStr);

        String courseCacheUpdateStr = "UPDATE mdl_course SET cacherev = (CASE WHEN cacherev IS NULL THEN " + Integer.valueOf(time) + " WHEN cacherev < " + Integer.valueOf(time) + " THEN " + Integer.valueOf(time) + " WHEN cacherev > " + Integer.valueOf(time) + " + 3600 THEN " + Integer.valueOf(time) + " ELSE cacherev + 1 END) WHERE id = '" + courseId + "'";
        String courseCacheUpdateResult = executeUpdate(conn, courseCacheUpdateStr);

        String quizInsertionStr = "INSERT INTO mdl_quiz (name,timeopen,timeclose,timelimit,overduehandling,graceperiod,grade,attempts,grademethod,questionsperpage,navmethod,shuffleanswers,preferredbehaviour,canredoquestions,attemptonlast,showuserpicture,decimalpoints,questiondecimalpoints,showblocks,subnet,delay1,delay2,browsersecurity,completionpass,completionattemptsexhausted,course,intro,introformat,timemodified,password,reviewattempt,reviewcorrectness,reviewmarks,reviewspecificfeedback,reviewgeneralfeedback,reviewrightanswer,reviewoverallfeedback) VALUES('" + quizName + "','0','0','0','autoabandon','0',10,'0','1','1','free','1','deferredfeedback','0','0','0','2','-1','0','','0','0','-','0','0','" + courseId + "','','1','" + time + "','','69904','4368','4368','4368','4368','4368','4368')";
        String quizInsertionResult = executeUpdate(conn, quizInsertionStr);
        quizId = quizInsertionResult;

        String quizSectionInsertionStr = "INSERT INTO mdl_quiz_sections (quizid,firstslot,heading,shufflequestions) VALUES('" + quizId + "','1','','0')";
        String quizSectionInsertionResult = executeUpdate(conn, quizSectionInsertionStr);
        courseModuleId = courseModuleInsertionResult;

        String courseModuleUpdateStr = "UPDATE mdl_course_modules SET instance = '" + quizId + "' WHERE id = '" + courseModuleId + "'";
        String courseModuleUpdateResult = executeUpdate(conn, courseModuleUpdateStr);

        String contextInsertionStr = "INSERT INTO mdl_context (contextlevel,instanceid,depth,path) VALUES('70','" + courseModuleId + "','0',NULL)";
        String contextInsertionResult = executeUpdate(conn, contextInsertionStr);
        contextId = contextInsertionResult;

        String contextSelectStr = "select path from mdl_context where instanceid = '" + courseId + "' and contextlevel = 50";
        String contextSelectResult = executeQuery(conn, contextSelectStr);
        contextPath = contextSelectResult;
        contextPath += "/" + contextId;
        depth = String.valueOf(contextPath.split("/").length - 1);

        String contextUpdateStr = "UPDATE mdl_context SET contextlevel = '70',instanceid = '" + courseModuleId + "',depth = '" + depth + "',path = '" + contextPath + "' WHERE id='" + contextId + "'";
        String contextUpdateResult = executeUpdate(conn, contextUpdateStr);

        String quizDeletStr = "DELETE FROM mdl_quiz_feedback WHERE quizid = '" + quizId + "'";
        String quizDeletResult = executeUpdate(conn, quizDeletStr);

        String quizFeedbackInsertionStr = "INSERT INTO mdl_quiz_feedback (quizid,feedbacktext,feedbacktextformat,mingrade,maxgrade) VALUES('" + quizId + "','','1','0',11)";
        String quizFeedbackInsertionResult = executeUpdate(conn, quizFeedbackInsertionStr);
        quizFeedbackId = quizFeedbackInsertionResult;

        String quizFeedbackUpdateStr = "UPDATE mdl_quiz_feedback SET feedbacktext = '' WHERE id = '" + quizFeedbackId + "'";
        String quizFeedbackUpdateResult = executeUpdate(conn, quizFeedbackUpdateStr);

        String gradeCategorySelectStr = "select id from mdl_grade_categories where courseid = '" + courseId + "'";
        String gradeCategorySelectResult = executeQuery(conn, gradeCategorySelectStr);
        if (gradeCategorySelectResult == null) {
            String gradeCategoryInsertionStr = "insert into mdl_grade_categories(courseid, parent, depth, path, fullname, aggregation, keephigh, droplow, aggregateonlygraded, aggregateoutcomes, timecreated, timemodified, hidden) VALUES('" + courseId + "', NULL, 1, NULL, '?', 11, 0, 0, 1, 0, '" + time + "', '" + time + "', 0)";
            String gradeCategoryInsertionResult = executeUpdate(conn, gradeCategoryInsertionStr);

            String gradeCategoryUpdateStr = "update mdl_grade_categories set path = '/" + gradeCategoryInsertionResult + "/' where id = " + gradeCategoryInsertionResult;
            String gradeCategoryUpdateResult = executeUpdate(conn, gradeCategoryUpdateStr);
            gradeCategory = gradeCategoryInsertionResult;
        } else {
            gradeCategory = gradeCategorySelectResult;
        }

        String sortOrderSelectStr = "select max(sortorder) from mdl_grade_items where courseid = '" + courseId + "'";
        String sortOrderSelectResult = executeQuery(conn, sortOrderSelectStr);
        sortOrder = (Integer.valueOf(sortOrderSelectResult) + 1) + "";

        String gradeItemInsertionStr = "INSERT INTO mdl_grade_items (courseid,categoryid,itemname,itemtype,itemmodule,iteminstance,itemnumber,iteminfo,idnumber,calculation,gradetype,grademax,grademin,scaleid,outcomeid,gradepass,multfactor,plusfactor,aggregationcoef,aggregationcoef2,sortorder,display,decimals,locked,locktime,needsupdate,weightoverride,timecreated,timemodified,hidden) VALUES('" + courseId + "','" + gradeCategory + "','" + quizName + "','mod','quiz','" + quizId + "','0',NULL,'',NULL,'1',10,'0',NULL,NULL,'0',1,'0','0','0','" + sortOrder + "','0',NULL,'0','0','1','0','" + time + "','" + time + "','0')";
        String gradeItemInsertionResult = executeUpdate(conn, gradeItemInsertionStr);
        gradeItemId = gradeItemInsertionResult;

        String gradeItemHistoryInsertionStr = "INSERT INTO mdl_grade_items_history (courseid,categoryid,itemname,itemtype,itemmodule,iteminstance,itemnumber,iteminfo,idnumber,calculation,gradetype,grademax,grademin,scaleid,outcomeid,gradepass,multfactor,plusfactor,aggregationcoef,aggregationcoef2,sortorder,display,decimals,locked,locktime,needsupdate,weightoverride,timemodified,hidden,action,oldid,source,loggeduser) VALUES('" + courseId + "','" + gradeCategory + "','" + quizName + "','mod','quiz','" + quizId + "','0',NULL,'',NULL,'1','10.00000','0.00000',NULL,NULL,'0.00000','1.00000','0.00000','0.00000','0.00000','" + sortOrder + "','0',NULL,'0','0','1','0','" + time + "','0','1','" + gradeItemId + "',NULL,'2')";
        String gradeItemHistoryInsertionResult = executeUpdate(conn, gradeItemHistoryInsertionStr);

        String gradeItemUpdateStr = "UPDATE mdl_grade_items SET needsupdate = '1' WHERE (itemtype='course' OR id='" + gradeItemId + "') AND courseid='" + courseId + "'";
        String gradeItemUpdateResult = executeUpdate(conn, gradeItemUpdateStr);

        String courseModuleUpdateStr2 = "UPDATE mdl_course_modules SET instance = '" + quizId + "' WHERE id = '" + courseModuleId + "'";
        String courseModuleUpdateResult2 = executeUpdate(conn, courseModuleUpdateStr2);

        String quizUpdateStr = "UPDATE mdl_quiz SET intro = '' WHERE id = '" + quizId + "'";
        String quizUpdateResult = executeUpdate(conn, quizUpdateStr);

        String sectionSelectStr = "select id from mdl_course_sections where course = '" + courseId + "' and section = 0";
        String sectionSelectResult = executeQuery(conn, sectionSelectStr);
        section = sectionSelectResult;

        String sequenceSelectStr = "select sequence from mdl_course_sections where course = '" + courseId + "' and id = '" + section + "'";
        String sequenceSelectResult = executeQuery(conn, sequenceSelectStr);
        sequence = sequenceSelectResult != null ? sequenceSelectResult + "," + courseModuleId : courseModuleId;

        String sectionUpdateStr = "UPDATE mdl_course_sections SET sequence = '" + sequence + "' WHERE id = '" + section + "'";
        String sectionUpdateResult = executeUpdate(conn, sectionUpdateStr);

        String courseModuleUpdateStr3 = "UPDATE mdl_course_modules SET section = '" + section + "' WHERE id = '" + courseModuleId + "'";
        String courseModuleUpdateResult3 = executeUpdate(conn, courseModuleUpdateStr3);

        courseCacheUpdateStr = "UPDATE mdl_course SET cacherev = (CASE WHEN cacherev IS NULL THEN " + Integer.valueOf(time) + " WHEN cacherev < " + Integer.valueOf(time) + " THEN " + Integer.valueOf(time) + " WHEN cacherev > " + Integer.valueOf(time) + " + 3600 THEN " + Integer.valueOf(time) + " ELSE cacherev + 1 END) WHERE id = '" + courseId + "'";
        courseCacheUpdateResult = executeUpdate(conn, courseCacheUpdateStr);

        String gradeItemUpdateStr2 = "UPDATE mdl_grade_items SET courseid = '" + courseId + "',categoryid = '" + gradeCategory + "',itemname = '" + quizName + "',itemtype = 'mod',itemmodule = 'quiz',iteminstance = '" + quizId + "',itemnumber = '0',iteminfo = NULL,idnumber = '',calculation = NULL,gradetype = '1',grademax = 10,grademin = 0,scaleid = NULL,outcomeid = NULL,gradepass = '0.00000',multfactor = 1,plusfactor = 0,aggregationcoef = 0,aggregationcoef2 = 0,sortorder = '" + sortOrder + "',display = '0',decimals = NULL,locked = '0',locktime = '0',needsupdate = '1',weightoverride = '0',timecreated = '" + time + "',timemodified = '" + time + "',hidden = '0' WHERE id='" + gradeItemId + "'";
        String gradeItemUpdateResult2 = executeUpdate(conn, gradeItemUpdateStr2);

        String gradeItemHistoryStr2 = "INSERT INTO mdl_grade_items_history (courseid,categoryid,itemname,itemtype,itemmodule,iteminstance,itemnumber,iteminfo,idnumber,calculation,gradetype,grademax,grademin,scaleid,outcomeid,gradepass,multfactor,plusfactor,aggregationcoef,aggregationcoef2,sortorder,display,decimals,locked,locktime,needsupdate,weightoverride,timemodified,hidden,action,oldid,source,loggeduser) VALUES('" + courseId + "','" + gradeCategory + "','" + quizName + "','mod','quiz','" + quizId + "','0',NULL,'',NULL,'1',10,0,NULL,NULL,'0.00000',1,0,0,0,'" + sortOrder + "','0',NULL,'0','0','1','0','" + time + "','0','2','" + gradeItemId + "',NULL,'2')";
        String gradeItemHistoryResult2 = executeUpdate(conn, gradeItemHistoryStr2);

        courseCacheUpdateStr = "UPDATE mdl_course SET cacherev = (CASE WHEN cacherev IS NULL THEN " + Integer.valueOf(time) + " WHEN cacherev < " + Integer.valueOf(time) + " THEN " + Integer.valueOf(time) + " WHEN cacherev > " + Integer.valueOf(time) + " + 3600 THEN " + Integer.valueOf(time) + " ELSE cacherev + 1 END) WHERE id = '" + courseId + "'";
        courseCacheUpdateResult = executeUpdate(conn, courseCacheUpdateStr);

        String gradeItemUpdateStr3 = "UPDATE mdl_grade_items SET needsupdate = '0' WHERE id = '" + gradeItemId + "'";
        String gradeItemUpdateResult3 = executeUpdate(conn, gradeItemUpdateStr3);
    }

    public static String executeUpdate(Connection conn, String queryStr) throws SQLException {
        PreparedStatement coursePreparedStatement = conn.prepareStatement(queryStr, Statement.RETURN_GENERATED_KEYS);
        int affectedRows = coursePreparedStatement.executeUpdate();
//        if (affectedRows == 0) {
//            System.out.println("No rows affected by: " + queryStr);
//        }
        ResultSet generatedKeys = coursePreparedStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return (generatedKeys.getLong(1)) + "";
        }
        return "0";
    }

    public static String executeQuery(Connection conn, String queryStr) throws SQLException {
        PreparedStatement coursePreparedStatement = conn.prepareStatement(queryStr, Statement.RETURN_GENERATED_KEYS);
        ResultSet resultSet = coursePreparedStatement.executeQuery();
        if (resultSet.next()) {
            Object obj = resultSet.getObject(1);
            return obj == null ? "0" : obj.toString();
        } else {
            return null;
        }
    }

    private static void createQuiz(int courseId) throws SQLException {
        SQLConnector.getInstance();
        Connection conn = SQLConnector.conn;
        String insertQueryStr = "insert into " +
                "mdl_quiz(course, name, intro, timeopen, timeclose, attempts, attemptonlast, grademethod, questionsperpage, " +
                "sumgrades, grade) " +
                "values (?, 'TestQuiz2', '-', 1455450300, 1455468300, 2, 1, 4, 15, 15, 15)";
        PreparedStatement coursePreparedStatement = conn.prepareStatement(insertQueryStr);
        coursePreparedStatement.setInt(1, courseId);
        coursePreparedStatement.executeUpdate();

        String feedbackQueryStr = "insert into " +
                "mdl_quiz_sections(quizid, firstslot)" +
                "values (10, 1)";
        PreparedStatement feedbackPreparedStatement = conn.prepareStatement(feedbackQueryStr);
        feedbackPreparedStatement.executeUpdate();

        String sectionQueryStr = "insert into " +
                "mdl_quiz_feedback(quizid, feedbacktextformat, feedbacktext, mingrade, maxgrade) " +
                "values (10, 1, '-', 0, 11)";
        PreparedStatement sectionPreparedStatement = conn.prepareStatement(sectionQueryStr);
        sectionPreparedStatement.executeUpdate();

        String moduleQueryStr = "insert into " +
                "mdl_course_modules(course, module, instance, section, added) " +
                "values (?, 16, 10, 293, 1455450332)";
        PreparedStatement modulePreparedStatement = conn.prepareStatement(moduleQueryStr);
        modulePreparedStatement.setInt(1, courseId);
        modulePreparedStatement.executeUpdate();

        String cSectionQueryStr = "insert into " +
                "mdl_course_sections(course, section, summaryformat, sequence, visible) " +
                "values (?, 2, 1, 174, 1)";
        PreparedStatement cSectionPreparedStatement = conn.prepareStatement(cSectionQueryStr);
        cSectionPreparedStatement.setInt(1, courseId);
        cSectionPreparedStatement.executeUpdate();

        String contextQueryStr = "insert into " +
                "mdl_context(contextlevel, instanceid, path, depth) " +
                "values (70, 174, '/1/966/976/977', 4)";
        PreparedStatement contextPreparedStatement = conn.prepareStatement(contextQueryStr);
        contextPreparedStatement.executeUpdate();

        conn.close();
    }

    private static void createCourses(int categoryId) throws SQLException {
        SQLConnector.getInstance();
        Connection conn = SQLConnector.conn;
        String insertQueryStr = "insert into " +
                "mdl_course(category, sortorder, fullname, shortname, idnumber, summary, summaryformat, format, " +
                "showgrades, startdate, timecreated, timemodified) " +
                "values (?, 60009, ?, ?, ?, '-', 1, 'weeks', 1, 1411936200, 1411936200, 1411936200)";
        PreparedStatement coursePreparedStatement = conn.prepareStatement(insertQueryStr);
        coursePreparedStatement.setInt(1, categoryId);
        coursePreparedStatement.setString(2, "TestCourse");
        coursePreparedStatement.setString(3, "TestCourse");
        coursePreparedStatement.setInt(4, 555);
        coursePreparedStatement.executeUpdate();
        conn.close();
    }

    private static void getQuestionTexts() throws SQLException {
        SQLConnector.getInstance();
        Connection conn = SQLConnector.conn;
        String courseQueryStr = "select distinct(qu.id), qu.questiontext from mdl_quiz_slots qs\n" +
                "inner join mdl_quiz q on qs.quizid = q.id\n" +
                "inner join mdl_question qu on qs.questionid = qu.id\n" +
                "inner join mdl_course c on q.course = c.id and c.idnumber = '2*9527'";
        PreparedStatement questionPreparedStatement = conn.prepareStatement(courseQueryStr);
        ResultSet questionResultSet = questionPreparedStatement.executeQuery();
        while (questionResultSet.next()) {
            int questionId = questionResultSet.getInt(1);
            String questionText = questionResultSet.getString(2);
            questionText = questionText.replaceAll("&nbsp;", "");
            questionText = questionText.replaceAll("\\<.*?>", "");
            System.out.println(questionId + "\t" + questionText);
        }

    }

    private static void insertSupportQuestions() throws SQLException {
        SQLConnector.getInstance();
        Connection conn = SQLConnector.conn;
        String courseQueryStr = "select q.id, c.shortname from mdl_quiz q " +
                "inner join mdl_course c on q.course = c.id and c.shortname in (" +
                "'m.babaei',\n" +
                "'f.asadian',\n" +
                "'m.dadgar',\n" +
                "'y.khari',\n" +
                "'b.hosseinpour',\n" +
                "'m.raesi',\n" +
                "'n.sadoghi',\n" +
                "'gh.gorji',\n" +
                "'m.mousavi',\n" +
                "'m.khanderoo',\n" +
                "'a.tooyserkani',\n" +
                "'h.sasanian',\n" +
                "'y.javan',\n" +
                "'h.shabanipour',\n" +
//                "'a.azarnia',\n" +
                "'sh.arsen',\n" +
                "'h.sedighpour',\n" +
//                "'a.khari',\n" +
                "'y.fallahi'\n" +
                ")";
        PreparedStatement coursePreparedStatement = conn.prepareStatement(courseQueryStr);
        ResultSet courseResultSet = coursePreparedStatement.executeQuery();

        String selectQueryStr = "select slot, questionid from mdl_quiz_slots where quizid = 810";
        PreparedStatement selectPreparedStatement = conn.prepareStatement(selectQueryStr);
        while (courseResultSet.next()) {
            int quizId = courseResultSet.getInt(1);
            String shortname = courseResultSet.getString(2);
            System.out.println(quizId + ", " + shortname);
            ResultSet selectResultSet = selectPreparedStatement.executeQuery();
            while (selectResultSet.next()) {
                int slot = selectResultSet.getInt(1);
                int questionId = selectResultSet.getInt(2);
                String insertQueryStr = "insert into mdl_quiz_slots(slot, quizid, page, requireprevious, questionid, maxmark) " +
                        "values(?, ?, 1, 0, ?, 1.0000000)";
                PreparedStatement insertPreparedStatement = conn.prepareStatement(insertQueryStr);
                insertPreparedStatement.setInt(1, slot);
                insertPreparedStatement.setInt(2, quizId);
                insertPreparedStatement.setInt(3, questionId);
                insertPreparedStatement.executeUpdate();
            }
        }
    }

    private static void enrolExtraUsers(String fileName) throws SQLException, IOException {
        SQLConnector.getInstance();
        Connection conn = SQLConnector.conn;
        RandomAccessFile file = new RandomAccessFile(fileName, "r");
        String line;
        while ((line = file.readLine()) != null) {
            String[] commaSeperatedValues = line.split(",");
            int courseId = Integer.parseInt(commaSeperatedValues[0]);
            String enrolIdQueryStr = "select id from mdl_enrol where courseid = ? and enrol = 'manual'";
            PreparedStatement enrolIdPreparedStatement = conn.prepareStatement(enrolIdQueryStr);
            enrolIdPreparedStatement.setInt(1, courseId);
            ResultSet enrolIdResultSet = enrolIdPreparedStatement.executeQuery();
            enrolIdResultSet.next();
            int enrolId = enrolIdResultSet.getInt(1);

            String contextIdQueryStr = "select id from mdl_context where instanceid = ? and contextlevel = 50";
            PreparedStatement contextIdPreparedStatement = conn.prepareStatement(contextIdQueryStr);
            contextIdPreparedStatement.setInt(1, courseId);
            ResultSet contextIdResultSer = contextIdPreparedStatement.executeQuery();
            contextIdResultSer.next();
            int contextId = contextIdResultSer.getInt(1);
            for (int i = 1; i < commaSeperatedValues.length; i++) {
                int userId = Integer.parseInt(commaSeperatedValues[i]);
                String enrolmentQueryStr = "insert into mdl_user_enrolments(status, enrolid, userid, timestart, timeend, modifierid, timecreated, timemodified) " +
                        "values " +
                        "(0, ?, ?, 1433973600, 0, 133, 1434017163, 1434017163)";
                PreparedStatement enrolmentPreparedStatement = conn.prepareStatement(enrolmentQueryStr);
                enrolmentPreparedStatement.setInt(1, enrolId);
                enrolmentPreparedStatement.setInt(2, userId);
                enrolmentPreparedStatement.executeUpdate();

                String contextQueryStr = "insert into mdl_role_assignments(roleid, contextid, userid, timemodified, modifierid) " +
                        "values " +
                        "(5, ?, ?, 1434017163, 133)";
                PreparedStatement contextPreparedStatement = conn.prepareStatement(contextQueryStr);
                contextPreparedStatement.setInt(1, contextId);
                contextPreparedStatement.setInt(2, userId);
                contextPreparedStatement.executeUpdate();
            }
        }
    }

    private static void enrolSupportUsers() throws IOException, SQLException {
        ArrayList<Integer> userIds = new ArrayList<Integer>();
        SQLConnector.getInstance();
        Connection conn = SQLConnector.conn;
        String alreadyEnroledUserIdsQueryStr = "select ue.userid from mdl_user_enrolments ue\n" +
                "inner join mdl_enrol e on ue.enrolid = e.id and e.courseid = 469";
        PreparedStatement alreadyEnroledUserIdsStmt = conn.prepareStatement(alreadyEnroledUserIdsQueryStr);
        ResultSet alreadyEnroledUserIdsResultSet = alreadyEnroledUserIdsStmt.executeQuery();

        while (alreadyEnroledUserIdsResultSet.next()) {
            int userId = alreadyEnroledUserIdsResultSet.getInt(1);
            userIds.add(userId);
        }
        for (Integer userId : userIds) {
            if (userId.equals(127) || userId.equals(126)) {
                continue;
            }
            String courseFinderQueryStr = "select c.id from mdl_course c\n" +
                    "inner join mdl_user u on c.shortname = u.username and u.id = ?";
            PreparedStatement courseFinderStmt = conn.prepareStatement(courseFinderQueryStr);
            courseFinderStmt.setInt(1, userId);
            ResultSet courseFinderResultSet = courseFinderStmt.executeQuery();
            courseFinderResultSet.next();
            int courseId = courseFinderResultSet.getInt(1);

            String enrolIdQueryStr = "select id from mdl_enrol where courseid = ? and enrol = 'manual'";
            PreparedStatement enrolIdPreparedStatement = conn.prepareStatement(enrolIdQueryStr);
            enrolIdPreparedStatement.setInt(1, courseId);
            ResultSet enrolIdResultSer = enrolIdPreparedStatement.executeQuery();
            enrolIdResultSer.next();
            int enrolId = enrolIdResultSer.getInt(1);

            String contextIdQueryStr = "select id from mdl_context where instanceid = ? and contextlevel = 50";
            PreparedStatement contextIdPreparedStatement = conn.prepareStatement(contextIdQueryStr);
            contextIdPreparedStatement.setInt(1, courseId);
            ResultSet contextIdResultSer = contextIdPreparedStatement.executeQuery();
            contextIdResultSer.next();
            int contextId = contextIdResultSer.getInt(1);

            for (Integer toBeEnrolledUserId : userIds) {
                if (!toBeEnrolledUserId.equals(userId)) {
                    try {
                        System.out.println(userId + ", " + toBeEnrolledUserId);
                        String enrolmentQueryStr = "insert into mdl_user_enrolments(status, enrolid, userid, timestart, timeend, modifierid, timecreated, timemodified) " +
                                "values " +
                                "(0, ?, ?, 1433973600, 0, 133, 1434017163, 1434017163)";
                        PreparedStatement enrolmentPreparedStatement = conn.prepareStatement(enrolmentQueryStr);
                        enrolmentPreparedStatement.setInt(1, enrolId);
                        enrolmentPreparedStatement.setInt(2, toBeEnrolledUserId);
                        enrolmentPreparedStatement.executeUpdate();

                        String contextQueryStr = "insert into mdl_role_assignments(roleid, contextid, userid, timemodified, modifierid) " +
                                "values " +
                                "(5, ?, ?, 1434017163, 133)";
                        PreparedStatement contextPreparedStatement = conn.prepareStatement(contextQueryStr);
                        contextPreparedStatement.setInt(1, contextId);
                        contextPreparedStatement.setInt(2, toBeEnrolledUserId);
                        contextPreparedStatement.executeUpdate();
                    } catch (MySQLIntegrityConstraintViolationException e) {
                        System.out.printf("User %d already enrolled in course %d\n", toBeEnrolledUserId, userId);
                    }
                }
            }
        }
    }

    private static void phase2Enrollment() throws IOException, SQLException {
        ArrayList<Integer> userIds;
        RandomAccessFile enrollmentFile = new RandomAccessFile("data/Enrollment.csv", "rw");
        String line;
        SQLConnector.getInstance();
        Connection conn = SQLConnector.conn;
        while ((line = enrollmentFile.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(line, ",");
            String referenceCourseIdNumber = tokenizer.nextToken();
            String alreadyEnroledUserIdsQueryStr = "select ue.userid from mdl_user_enrolments ue\n" +
                    "inner join mdl_enrol e on ue.enrolid = e.id\n" +
                    "inner join mdl_course c on e.courseid = c.id and c.idnumber = ?";
            PreparedStatement alreadyEnroledUserIdsStmt = conn.prepareStatement(alreadyEnroledUserIdsQueryStr);
            alreadyEnroledUserIdsStmt.setString(1, referenceCourseIdNumber);
            ResultSet alreadyEnroledUserIdsResultSet = alreadyEnroledUserIdsStmt.executeQuery();
            userIds = new ArrayList<Integer>();
            while (alreadyEnroledUserIdsResultSet.next()) {
                int userId = alreadyEnroledUserIdsResultSet.getInt(1);
                userIds.add(userId);
            }
            while (tokenizer.hasMoreTokens()) {
                String nextCourseIdNumber = tokenizer.nextToken();
                String courseFinderQueryStr = "select id from mdl_course\n" +
                        "where idnumber = ?";
                PreparedStatement courseFinderStmt = conn.prepareStatement(courseFinderQueryStr);
                courseFinderStmt.setString(1, nextCourseIdNumber);
                ResultSet courseFinderResultSet = courseFinderStmt.executeQuery();
                courseFinderResultSet.next();
                int courseId = 0;
                try {
                    courseId = courseFinderResultSet.getInt(1);
                } catch (SQLException e) {
                    System.out.println("%%%%%%%%%%%%%%%%%%%" + nextCourseIdNumber);
                }

                String enrolIdQueryStr = "select id from mdl_enrol where courseid = ? and enrol = 'manual'";
                PreparedStatement enrolIdPreparedStatement = conn.prepareStatement(enrolIdQueryStr);
                enrolIdPreparedStatement.setInt(1, courseId);
                ResultSet enrolIdResultSer = enrolIdPreparedStatement.executeQuery();
                enrolIdResultSer.next();
                int enrolId = enrolIdResultSer.getInt(1);

                String contextIdQueryStr = "select id from mdl_context where instanceid = ? and contextlevel = 50";
                PreparedStatement contextIdPreparedStatement = conn.prepareStatement(contextIdQueryStr);
                contextIdPreparedStatement.setInt(1, courseId);
                ResultSet contextIdResultSer = contextIdPreparedStatement.executeQuery();
                contextIdResultSer.next();
                int contextId = contextIdResultSer.getInt(1);

                for (Integer toBeEnrolledUserId : userIds) {
                    try {
                        System.out.println(nextCourseIdNumber + ", " + toBeEnrolledUserId);
                        String enrolmentQueryStr = "insert into mdl_user_enrolments(status, enrolid, userid, timestart, timeend, modifierid, timecreated, timemodified) " +
                                "values " +
                                "(0, ?, ?, 1433973600, 0, 133, 1434017163, 1434017163)";
                        PreparedStatement enrolmentPreparedStatement = conn.prepareStatement(enrolmentQueryStr);
                        enrolmentPreparedStatement.setInt(1, enrolId);
                        enrolmentPreparedStatement.setInt(2, toBeEnrolledUserId);
                        enrolmentPreparedStatement.executeUpdate();

                        String contextQueryStr = "insert into mdl_role_assignments(roleid, contextid, userid, timemodified, modifierid) " +
                                "values " +
                                "(5, ?, ?, 1434017163, 133)";
                        PreparedStatement contextPreparedStatement = conn.prepareStatement(contextQueryStr);
                        contextPreparedStatement.setInt(1, contextId);
                        contextPreparedStatement.setInt(2, toBeEnrolledUserId);
                        contextPreparedStatement.executeUpdate();
                    } catch (MySQLIntegrityConstraintViolationException e) {
                        System.out.printf("User %d already enrolled in course %s\n", toBeEnrolledUserId, nextCourseIdNumber);
                    }
                }
            }
        }
    }

    private static void analyzeData() throws IOException, SQLException {
        analyzeTotal();
        analyzeBaseOnRoles();
        analyzeBasedOnKPIs();
    }

    private static void analyzeTotal() throws SQLException, IOException {
        Map<String, Map<Integer, QuestionResult>> allUnitQuestionResults = fetchDataFromDB(UNIT_ID);
        DataAnalyzer.analyzeAssessmentData(allUnitQuestionResults, null, null);
    }

    private static void analyzeBasedOnKPIs() throws IOException, SQLException {
        Map<String, Map<Integer, QuestionResult>> allUnitQuestionResults;
        String line;
        InputStream fis = new FileInputStream("resources/kpi.txt");
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        while ((line = br.readLine()) != null) {
            String kpi = line.split(":")[0];
            String[] questions = line.split(":")[1].split(";");
            ArrayList<Integer> questionsIdPerKPI = new ArrayList<Integer>();
            for (String question : questions) {
                questionsIdPerKPI.add(Integer.valueOf(question));
            }
            allUnitQuestionResults = fetchDataFromDB(UNIT_ID);
            for (String assessee : allUnitQuestionResults.keySet()) {
                Map<Integer, QuestionResult> questionResultMap = allUnitQuestionResults.get(assessee);
                ArrayList<Integer> toBeRemoved = new ArrayList<Integer>();
                for (Integer questionId : questionResultMap.keySet()) {
                    if (!questionsIdPerKPI.contains(questionId)) {
                        toBeRemoved.add(questionId);
                    }
                }
                for (Integer id : toBeRemoved) {
                    questionResultMap.remove(id);
                }
            }
            DataAnalyzer.analyzeAssessmentData(allUnitQuestionResults, null, kpi);
        }
    }

    private static void analyzeBaseOnRoles() throws SQLException, IOException {
        Map<String, Map<Integer, QuestionResult>> allUnitQuestionResults;
        for (Role role : Role.values()) {
            allUnitQuestionResults = fetchDataFromDB(UNIT_ID);
            for (String assessee : allUnitQuestionResults.keySet()) {
                Map<Integer, QuestionResult> questionResultMap = allUnitQuestionResults.get(assessee);
                for (Integer questionId : questionResultMap.keySet()) {
                    QuestionResult result = questionResultMap.get(questionId);
                    List<AssessmentScore> toBeRemoved = new ArrayList<AssessmentScore>();
                    for (AssessmentScore assessmentScore : result.getSocres()) {
                        if (assessmentScore.getAssessor().getRole() != role) {
                            toBeRemoved.add(assessmentScore);
                        }
                    }
                    for (AssessmentScore assessmentScore : toBeRemoved) {
                        result.getSocres().remove(assessmentScore);
                    }
                }
            }
            DataAnalyzer.analyzeAssessmentData(allUnitQuestionResults, role, null);
        }
    }

    public static Role findRole(String unitName, String assesseeName) throws IOException {
        String person;
        FileInputStream reader = new FileInputStream("resources/roles/" + unitName + "_roles.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(reader, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        while ((person = bufferedReader.readLine()) != null) {
            String memberName = person.substring(0, person.indexOf(','));
            if (assesseeName.equals(memberName)) {
                return Role.getRole(person.substring(person.indexOf(',') + 1));
            }
        }
        return null;
    }

    private static Map<String, Map<Integer, QuestionResult>> fetchDataFromDB(Integer unitId) throws SQLException {
        String queryStr = "select c.fullname assessee, concat(u.firstname, ' ', u.lastname) assessor, u.city role, que.id, qas.fraction\n" +
                "from mdl_quiz q\n" +
                "inner join mdl_course c on q.course = c.id and c.category = ?\n" +
                "inner join mdl_quiz_attempts qat on qat.quiz = q.id\n" +
                "inner join mdl_question_usages quba on qat.uniqueid = quba.id\n" +
                "LEFT JOIN mdl_question_attempts qa ON qa.questionusageid = quba.id\n" +
                "inner join mdl_question que on qa.questionid = que.id\n" +
                "LEFT JOIN mdl_question_attempt_steps qas ON qas.questionattemptid = qa.id and qas.fraction is not null\n" +
                "inner join mdl_user u on qas.userid = u.id\n" +
                "LEFT JOIN mdl_question_attempt_step_data qasd ON qasd.attemptstepid = qas.id";
        System.out.println(queryStr);
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        statement.setInt(1, unitId);
        ResultSet resultSet = statement.executeQuery();
        Map<String, Map<Integer, QuestionResult>> allUnitQuestionResults = new HashMap<String, Map<Integer, QuestionResult>>();
        while (resultSet.next()) {
            String assessee = resultSet.getString(1);
            String assessor = resultSet.getString(2);
            String assessorRole = resultSet.getString(3);
            Integer quesitonId = resultSet.getInt(4);
            double score = resultSet.getDouble(5);
            Role role = Role.getRole(assessorRole);
            if (role != null) {
                List<AssessmentScore> assessmentScores = new ArrayList<AssessmentScore>();
                AssessmentScore assessmentScore = new AssessmentScore();
                Person person = new Person(assessor, role);
                assessmentScore.setAssessor(person);
                assessmentScore.setScore(score);
                if (!allUnitQuestionResults.containsKey(assessee)) {
                    Map<Integer, QuestionResult> questionResultsMap = new HashMap<Integer, QuestionResult>();
                    assessmentScores.add(assessmentScore);
                    QuestionResult result = new QuestionResult(quesitonId, assessmentScores);
                    questionResultsMap.put(result.getQuestionCode(), result);
                    allUnitQuestionResults.put(assessee, questionResultsMap);
                } else {
                    Map<Integer, QuestionResult> questionResultsMap = allUnitQuestionResults.get(assessee);
                    if (!questionResultsMap.containsKey(quesitonId)) {
                        assessmentScores.add(assessmentScore);
                        QuestionResult result = new QuestionResult(quesitonId, assessmentScores);
                        questionResultsMap.put(result.getQuestionCode(), result);
                        allUnitQuestionResults.put(assessee, questionResultsMap);
                    } else {
                        QuestionResult questionResult = questionResultsMap.get(quesitonId);
                        assessmentScores = questionResult.getSocres();
                        assessmentScores.add(assessmentScore);
                        QuestionResult result = new QuestionResult(quesitonId, assessmentScores);
                        questionResultsMap.replace(result.getQuestionCode(), result);
                        allUnitQuestionResults.replace(assessee, questionResultsMap);
                    }
                }
            }
        }
        return allUnitQuestionResults;
    }

    private static Role fillQuestionResults() {
        Role assesseeRole = Role.getRole(rawExtractedData.get(0).get(0));
        ArrayList<String> roles = rawExtractedData.get(1);
        ArrayList<String> lastNames = rawExtractedData.get(2);
        ArrayList<String> firstNames = rawExtractedData.get(3);
        for (int i = 4; i < rawExtractedData.size(); i++) {
            ArrayList<String> row = rawExtractedData.get(i);
            QuestionResult questionResult = new QuestionResult();
            List<AssessmentScore> scores = new ArrayList<AssessmentScore>();
            questionResult.setQuestionCode(Integer.valueOf(row.get(0)));
            for (int j = 1; j < row.size(); j++) {
                String cell = row.get(j);
                AssessmentScore score = new AssessmentScore();
                Person assessor = new Person();
                assessor.setFirstName(firstNames.get(j));
                assessor.setLastName(lastNames.get(j));
                assessor.setRole(Role.getRole(roles.get(j)));
                score.setAssessor(assessor);
                score.setScore(!cell.equals("") ? Double.valueOf(cell) : 0);
                if (score.getScore() != 0) {
                    scores.add(score);
                }
            }
            questionResult.setSocres(scores);
            questionResults.add(questionResult);
        }
        return assesseeRole;
    }
}

class UserNameDS {
    private long courseId;
    private String englishName;
    private String persianName;

    UserNameDS(long courseId, String englishName, String persianName) {
        this.courseId = courseId;
        this.englishName = englishName;
        this.persianName = persianName;
    }

    long getCourseId() {
        return courseId;
    }

    void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    String getEnglishName() {
        return englishName;
    }

    void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    String getPersianName() {
        return persianName;
    }

    void setPersianName(String persianName) {
        this.persianName = persianName;
    }
}

