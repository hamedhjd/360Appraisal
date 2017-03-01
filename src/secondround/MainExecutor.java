package secondround;

import model.SQLConnector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 10/21/16
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainExecutor {
    private static final String CATEGORY_SELF = "SELF";
    private static final String CATEGORY_MANAGER = "MANAGER";
    private static final String CATEGORY_COLLEAGUE = "COLLEAGUE";
    private static final String CATEGORY_SUBSET = "SUBSET";
    private static final String CATEGORY_OUT = "OUT";
    private static final Map<String, String> categoryMapping = new LinkedHashMap<String, String>();
    private static final Map<String, String> stepMapping = new LinkedHashMap<String, String>();
    static final List<String> allCategories = new ArrayList<String>();

    static {
        allCategories.add(CATEGORY_SELF);
        allCategories.add(CATEGORY_MANAGER);
        allCategories.add(CATEGORY_COLLEAGUE);
        allCategories.add(CATEGORY_SUBSET);
        allCategories.add(CATEGORY_OUT);

        categoryMapping.put(CATEGORY_SELF, "خود ارزیابی");
        categoryMapping.put(CATEGORY_MANAGER, "همکاران مافوق");
        categoryMapping.put(CATEGORY_COLLEAGUE, "همکاران هم‌رده");
        categoryMapping.put(CATEGORY_SUBSET, "همکاران زیرمجموعه");
        categoryMapping.put(CATEGORY_OUT, "همکاران برون بخشی");

        stepMapping.put("1", "اولین");
        stepMapping.put("2", "دومین");
        stepMapping.put("3", "سومین");
        stepMapping.put("4", "چهارمین");
        stepMapping.put("5", "پنجمین");
        stepMapping.put("6", "ششمین");
        stepMapping.put("7", "هفتمین");
        stepMapping.put("8", "هشتمین");
    }

    public static void main(String[] args) throws Exception {
        try {
            String step = "2";
            String deliveryDate = "اسفند ۹۵";
            checkValidity();
            List<String> userIds = findAllUserIds();
//            List<String> userIds = new ArrayList<String>();
//            userIds.add("6031");
//            userIds.add("6358");
//            userIds.add("6101");
            generateKarnames(step, userIds, deliveryDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkValidity() throws Exception {
        String queryStr = "select ur.userId from cal_user_role ur where ur.userId not in (select idnumber from mdl_user)";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        ResultSet resultSet = statement.executeQuery();
        boolean err = false;
        while (resultSet.next()) {
            err = true;
            System.out.println("Non-existent user id in mdl_user table: " + resultSet.getString(1));
        }
        if (err) {
            throw new Exception("Check Validity Exception!");
        }
        System.out.println("Check Validation Successful, Generating Karnames...");
    }

    private static void generateKarnames(String step, List<String> userIds, String deliveryDate) throws Exception {
        List<SelfKarname> selfKarnames = new ArrayList<SelfKarname>();
        List<ModirKarname> modirKarnames = new ArrayList<ModirKarname>();
        for (String userId : userIds) {
            try {
                System.out.println("=============================>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Generating Karname For UserId: " + userId);
                Map<String, Map<String, BigDecimal>> questionMianginVazniPerCategory_step = caculateQuestionGradesPerUserPerCategory(step, userId);
                Map<String, Map<String, BigDecimal>> kpiMianginVazni_step = calculateTotalGradePerUserPerKPIPerCategory(questionMianginVazniPerCategory_step);
                Map<String, BigDecimal> totalScore_step = calculateTotalGradePerUserPerCategory(userId, kpiMianginVazni_step);

                SelfKarname selfKarname = generateSelfKarname(userId, kpiMianginVazni_step, totalScore_step);
                ModirKarname modirKarname = generateModirKarname(selfKarname, kpiMianginVazni_step, totalScore_step);

                selfKarnames.add(selfKarname);
                modirKarnames.add(modirKarname);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("***************************************************** All Karnames Has Been Created on RAM! *****************************************************");

        fillSectionKPIAverageScore(selfKarnames);
        fillSectionCategoryAverageScore(modirKarnames);

        for (SelfKarname karname : selfKarnames) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++ Generating Self Karname For UserId: " + karname.getUserInfo().getUserId());
            makeHTMLOutOfSelfKarname(karname, stepMapping.get(step), deliveryDate);
        }
        for (ModirKarname karname : modirKarnames) {
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Generating Modir Karname For UserId: " + karname.getSelfKarname().getUserInfo().getUserId());
            makeHTMLOutOfModirKarname(karname, stepMapping.get(step), deliveryDate);
        }
    }

    private static ModirKarname generateModirKarname(SelfKarname selfKarname, Map<String, Map<String, BigDecimal>> kpiMianginVazni_step, Map<String, BigDecimal> totalScore_step) {
        return new ModirKarname(selfKarname, kpiMianginVazni_step, totalScore_step);
    }

    private static void fillSectionKPIAverageScore(List<SelfKarname> selfKarnames) {
        Map<String, Map<String, BigDecimal>> sectionKPIAverage = new LinkedHashMap<String, Map<String, BigDecimal>>();
        Map<String, Map<String, BigDecimal>> sectionKPIAverageSum = new LinkedHashMap<String, Map<String, BigDecimal>>();
        Map<String, Map<String, BigDecimal>> sectionKPIAverageCount = new LinkedHashMap<String, Map<String, BigDecimal>>();
        for (SelfKarname karname : selfKarnames) {
            if (sectionKPIAverageSum.containsKey(karname.getUserInfo().getSection())) {
                Map<String, BigDecimal> kpiAverageSum = sectionKPIAverageSum.get(karname.getUserInfo().getSection());
                Map<String, BigDecimal> kpiAverageCount = sectionKPIAverageCount.get(karname.getUserInfo().getSection());
                for (KPIScoreInfo kpiScoreInfo : karname.getKpisScoreInfo()) {
                    BigDecimal karnameScore = BigDecimal.ZERO;
                    if (!kpiScoreInfo.getScore().equals("-")) {
                        karnameScore = new BigDecimal(kpiScoreInfo.getScore());
                    }
                    if (kpiAverageSum.containsKey(kpiScoreInfo.getKpiName())) {
                        BigDecimal score = kpiAverageSum.get(kpiScoreInfo.getKpiName());
                        BigDecimal count = kpiAverageCount.get(kpiScoreInfo.getKpiName());
                        kpiAverageSum.put(kpiScoreInfo.getKpiName(), score.add(karnameScore));
                        if (!karnameScore.equals(BigDecimal.ZERO)) {
                            kpiAverageCount.put(kpiScoreInfo.getKpiName(), count.add(BigDecimal.ONE));
                        }
                    } else {
                        kpiAverageSum.put(kpiScoreInfo.getKpiName(), karnameScore);
                        if (!karnameScore.equals(BigDecimal.ZERO)) {
                            kpiAverageCount.put(kpiScoreInfo.getKpiName(), BigDecimal.ONE);
                        } else {
                            kpiAverageCount.put(kpiScoreInfo.getKpiName(), BigDecimal.ZERO);
                        }
                    }
                }
            } else {
                Map<String, BigDecimal> kpiAverageSum = new LinkedHashMap<String, BigDecimal>();
                Map<String, BigDecimal> kpiAverageCount = new LinkedHashMap<String, BigDecimal>();
                sectionKPIAverageSum.put(karname.getUserInfo().getSection(), kpiAverageSum);
                sectionKPIAverageCount.put(karname.getUserInfo().getSection(), kpiAverageCount);
                for (KPIScoreInfo kpiScoreInfo : karname.getKpisScoreInfo()) {
                    if (!kpiScoreInfo.getScore().equals("-")) {
                        BigDecimal karnameScore = new BigDecimal(kpiScoreInfo.getScore());
                        kpiAverageSum.put(kpiScoreInfo.getKpiName(), karnameScore);
                        kpiAverageCount.put(kpiScoreInfo.getKpiName(), BigDecimal.ONE);
                    }
                }
            }
        }
        for (String section : sectionKPIAverageSum.keySet()) {
            HashMap<String, BigDecimal> kpiAverage = new LinkedHashMap<String, BigDecimal>();
            Map<String, BigDecimal> kpiAverageSum = sectionKPIAverageSum.get(section);
            for (String kpiName : kpiAverageSum.keySet()) {
                if (!sectionKPIAverageCount.get(section).get(kpiName).equals(BigDecimal.ZERO)) {
                    kpiAverage.put(kpiName, kpiAverageSum.get(kpiName).divide(sectionKPIAverageCount.get(section).get(kpiName), RoundingMode.FLOOR));
                } else {
                    kpiAverage.put(kpiName, BigDecimal.ZERO);
                }
            }
            sectionKPIAverage.put(section, kpiAverage);
        }
        for (SelfKarname karname : selfKarnames) {
            String section = karname.getUserInfo().getSection();
            Map<String, BigDecimal> kpisAverage = sectionKPIAverage.get(section);
            for (KPIScoreInfo scoreInfo : karname.getKpisScoreInfo()) {
                scoreInfo.setAverageScore(kpisAverage.get(scoreInfo.getKpiName()) + "");
            }
        }
    }

    private static void makeHTMLOutOfSelfKarname(SelfKarname karname, String step, String deliveryDate) throws Exception {
        String pathName = "C:\\360\\self\\" + karname.getUserInfo().getSection();
        File dir = new File(pathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        byte[] out = generateSelfResultFile(karname, step, deliveryDate);
        File file = new File(pathName + "\\" + karname.getUserInfo().getUserName() + ".html");
        file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(out);
        outputStream.close();
    }

    private static void makeHTMLOutOfModirKarname(ModirKarname karname, String step, String deliveryDate) throws Exception {
        String pathName = "C:\\360\\modir\\" + karname.getSelfKarname().getUserInfo().getSection();
        File dir = new File(pathName);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        byte[] out = generateModirResultFile(karname, step, deliveryDate);
        File file = new File(pathName + "\\" + karname.getSelfKarname().getUserInfo().getUserName() + ".html");
        file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(out);
        outputStream.close();
    }

    private static String assertNullity(Object inputObj) {
        return inputObj != null ? inputObj.toString() : "-";
    }

    private static byte[] generateSelfResultFile(SelfKarname karname, String step, String deliveryDate) throws Exception {
        String firstName = assertNullity(karname.getUserInfo().getFirstName());
        String lastName = assertNullity(karname.getUserInfo().getLastName());
        String personnelCode = assertNullity(karname.getUserInfo().getUserId());
        String unit = assertNullity(karname.getUserInfo().getUnit());
        String section = assertNullity(karname.getUserInfo().getSection());
        String position = assertNullity(karname.getUserInfo().getPosition());

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        String file = "<html  dir =\"rtl\">\n" +
                "<meta charset=\"UTF-8\"><style>\n" +
                "@font-face {\n" +
                "  font-family: 'IRANSansWeb';\n" +
                "   src: url('IRANSansWeb.woff');\n" +
                "}\n" +
                "\n" +
                "\n" +
                "*{\n" +
                "\tfont-family: 'B Yekan';\n" +
                "\tfont-size: 13;\n" +
                "\tbox-sizing: border-box;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "body {\n" +
                "  background: #fff9e7;\n" +
                "}\n" +
                "img {\n" +
                "float:right;\n" +
                "width:110px;\n" +
                "margin-right: 180px;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "p{\n" +
                "margin-right: 7%;\n" +
                "margin-bottom: 20px;\n" +
                "}\n" +
                "\n" +
                "h1{\n" +
                "font-weight: bold;\n" +
                "font-size:15;\n" +
                "text-align: center;\n" +
                "margin-top:20px;\n" +
                "margin-left:150px;\n" +
                "}\n" +
                "h2{\n" +
                "font-weight: bold;\n" +
                "font-size:13;\n" +
                "text-align: center;\n" +
                "margin-left:150px;\n" +
                "}\n" +
                "\n" +
                "table {\n" +
                "  padding-right:20%;\n" +
                "  padding-left:20%;\n" +
                "  border-collapse: collapse;\n" +
                "  border: 1px solid #009A6F;\n" +
                "  margin: 50px auto;\n" +
                "  background: white;\n" +
                "}\n" +
                "th {\n" +
                "  background: #174538;\n" +
                "  height: 54px;\n" +
                "  font-weight: lighter;\n" +
                "  color: white;\n" +
                "  border: 1px solid #009A6F;\n" +
                "  transition: all 0.2s;\n" +
                "}\n" +
                "tr {\n" +
                "  border-bottom: 1px solid #009A6F;\n" +
                "}\n" +
                "\n" +
                "td {\n" +
                "  border-right: 1px solid #009A6F;\n" +
                "  padding: 10px;\n" +
                "  transition: all 0.2s;\n" +
                "}\n" +
                "\n" +
                "td input {\n" +
                "  font-size: 14px;\n" +
                "  background: none;\n" +
                "  outline: none;\n" +
                "  border: 0;\n" +
                "  display: table-cell;\n" +
                "  height: 100%;\n" +
                "  width: 100%;\n" +
                "}\n" +
                "</style>" +
                "" +
                "<head>\n" +
                "<title>نتايج " + step + " دوره ارزيابي 360 درجه</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<image src=\"data:image/png;base64,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\"/><h1>نتايج " + step + " دوره ارزيابي 360 درجه همکاران</h1><h2>تاريخ ارائه کارنامه : " + deliveryDate + "</h2><div ><table class='table_360'>\n" +
                "<thead>\n" +
                "<tr><th colspan=\"6\">مشخصات همکار</th></tr>    <tr><th>\n" +
                "نام</th>\n" +
                "<th>\n" +
                "نام خانوادگي</th>\n" +
                "<th>\n" +
                "کد پرسنلي</th>\n" +
                "<th>\n" +
                "واحد</th>\n" +
                "<th>\n" +
                "بخش</th>\n" +
                "<th>\n" +
                "سمت</th>\n" +
                "</tr></thead><tr><td>" + firstName + "</td><td>" + lastName + "</td><td>" + personnelCode +
                "</td><td>" + unit + "</td><td>" + section + "</td><td>" + position + "</td></tr></table></div><div ><table class='table_360'>\n" +
                "<thead>\n" +
                "<tr><th colspan=\"5\" >امتياز کسب شده در هريک از شاخص\u200Cهاي ارزيابي</th></tr>    <tr><th>\n" +
                "رديف\n" +
                "</th>\n" +
                "<th>\n" +
                "شاخص هاي ارزيابي\n" +
                "</th>\n" +
                "<th>\n" +
                "امتياز ارزیابی سایرین\n" +
                "</th>\n" +
                "<th>\n" +
                "امتیاز خود ارزیابی\n" +
                "</th>\n" +
                "<th>\n" +
                "ميانگين امتياز در بخش</th>\n" +
                "</tr></thead>";

        List<KPIScoreInfo> kpisScoreInfo = karname.getKpisScoreInfo();

        List<String> allKPIs = findAllKPIs();

        for (String kpiCode : allKPIs) {
            KPIScoreInfo kpiScoreInfo = null;
            for (KPIScoreInfo scoreInfo : kpisScoreInfo) {
                if (scoreInfo.getKpiCode().equals(kpiCode)) {
                    kpiScoreInfo = scoreInfo;
                }
            }
            if (kpiScoreInfo != null) {
                String index = kpiScoreInfo.getKpiCode();
                String kpiName = assertNullity(kpiScoreInfo.getKpiName());
                String previousScore = assertNullity(kpiScoreInfo.getPreviousScore());
                String score = assertNullity(kpiScoreInfo.getScore());
                String scoreSelf = assertNullity(kpiScoreInfo.getScoreSelf());
                String averageScore = assertNullity(kpiScoreInfo.getAverageScore());
                file += ("<tr>\n" +
                        "<td>" + index + "</td>\n" +
                        "<td>\n" + kpiName + "</td>\n" +
                        "<td>" + score + "</td>\n" +
                        "<td>" + scoreSelf + "</td>\n" +
                        "<td>" + averageScore + "</td>\n" +
                        "</tr>");
            } else {
                String index = kpiCode;
                String kpiName = findKPIValueByKPICode(kpiCode);
                String previousScore = "-";
                String score = "-";
                String scoreSelf = "-";
                String averageScore = "-";
                file += ("<tr>\n" +
                        "<td>" + index + "</td>\n" +
                        "<td>\n" + kpiName + "</td>\n" +
                        "<td>" + score + "</td>\n" +
                        "<td>" + scoreSelf + "</td>\n" +
                        "<td>" + averageScore + "</td>\n" +
                        "</tr>");
            }
        }

        String totalScore = assertNullity(karname.getTotalScoreInfo().getTotalScore());
        String totalSelfScore = assertNullity(karname.getTotalScoreInfo().getTotalSelfScore());
        String totalIncompleteForms = assertNullity(karname.getTotalScoreInfo().getIncompleteFormsCount());

        file += ("</table></div><div ><table class='table_360'>\n" +
                "<thead>\n" +
                "<th>\n" + "امتياز کلي ارزيابي</th>\n" +
                "<th>\n" + "امتياز کلي خود ارزيابي</th>\n" +
                "<th>\n" + "تعداد فرم\u200Cهاي ارزيابي تکميل نشده توسط همکار</th>\n" +
                "</thead>" +


                "<tr>" +
                "<td>" + totalScore + "</td>" +
                "<td>" + totalSelfScore + "</td>" +
                "<td>" + totalIncompleteForms + "</td>" +
                "</tr>" +


                "</table></div><p>\n" +
                "نکات قابل توجه:\n" +
                "</p>\n" +
                "<p>\n" +
                "۱. حداکثر امتياز قابل کسب توسط همکاران، عدد 100 مي\u200Cباشد.\n" +
                "<br/>\n" +
                "۲. تعداد فرم\u200Cهايي که فرد اقدام به تکميل آن براي ساير همکاران ننموده\u200Cاست در قسمت \" تعداد فرم\u200Cهاي ارزيابي تکميل نشده توسط همکار\" درج شده\u200Cاست.\n" +
                "<br/>\n" +
                "۳. ميانگين امتياز شاخص\u200Cها با \"امتياز کلي ارزيابي\" به دليل اعمال ضرايب در شاخص\u200Cها، اندکي متفاوت مي\u200Cباشد.\n" +
                "</p></body>\n" +
                "</html>");
        buffer.write(file.getBytes("utf-8"));

        return buffer.toByteArray();
    }

    private static byte[] generateModirResultFile(ModirKarname modirKarname, String step, String deliveryDate) throws Exception {
        SelfKarname karname = modirKarname.getSelfKarname();
        String firstName = assertNullity(karname.getUserInfo().getFirstName());
        String lastName = assertNullity(karname.getUserInfo().getLastName());
        String personnelCode = assertNullity(karname.getUserInfo().getUserId());
        String unit = assertNullity(karname.getUserInfo().getUnit());
        String section = assertNullity(karname.getUserInfo().getSection());
        String position = assertNullity(karname.getUserInfo().getPosition());

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        String newFile = "<html  dir =\"rtl\">\n" +
                "<meta charset=\"UTF-8\"><style>\n" +
                "@font-face {\n" +
                "  font-family: 'IRANSansWeb';\n" +
                "   src: url('IRANSansWeb.woff');\n" +
                "}\n" +
                "\n" +
                "\n" +
                "*{\n" +
                "\tfont-family: 'B Yekan';\n" +
                "\tfont-size: 13;\n" +
                "\tbox-sizing: border-box;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "body {\n" +
                "  background: #fff9e7;\n" +
                "}\n" +
                "img {\n" +
                "float:right;\n" +
                "width:110px;\n" +
                "margin-right: 180px;\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "p{\n" +
                "margin-right: 7%;\n" +
                "margin-bottom: 20px;\n" +
                "}\n" +
                "\n" +
                "h1{\n" +
                "font-weight: bold;\n" +
                "font-size:15;\n" +
                "text-align: center;\n" +
                "margin-top:20px;\n" +
                "margin-left:150px;\n" +
                "}\n" +
                "h2{\n" +
                "font-weight: bold;\n" +
                "font-size:13;\n" +
                "text-align: center;\n" +
                "margin-left:150px;\n" +
                "}\n" +
                "\n" +
                "table {\n" +
                "  padding-right:20%;\n" +
                "  padding-left:20%;\n" +
                "  border-collapse: collapse;\n" +
                "  border: 1px solid #009A6F;\n" +
                "  margin: 50px auto;\n" +
                "  background: white;\n" +
                "}\n" +
                "th {\n" +
                "  background: #174538;\n" +
                "  height: 54px;\n" +
                "  font-weight: lighter;\n" +
                "  color: white;\n" +
                "  border: 1px solid #009A6F;\n" +
                "  transition: all 0.2s;\n" +
                "}\n" +
                "tr {\n" +
                "  border-bottom: 1px solid #009A6F;\n" +
                "}\n" +
                "\n" +
                "td {\n" +
                "  border-right: 1px solid #009A6F;\n" +
                "  padding: 10px;\n" +
                "  transition: all 0.2s;\n" +
                "}\n" +
                "\n" +
                "td input {\n" +
                "  font-size: 14px;\n" +
                "  background: none;\n" +
                "  outline: none;\n" +
                "  border: 0;\n" +
                "  display: table-cell;\n" +
                "  height: 100%;\n" +
                "  width: 100%;\n" +
                "}\n" +
                "</style><head>\n";

        newFile += "<title>نتايج " + step + " دوره ارزيابي 360 درجه</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<image src=\"data:image/png;base64,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\"/><h1>نتايج " + step + " دوره ارزيابي 360 درجه همکاران</h1><h2>تاريخ ارائه کارنامه : " + deliveryDate + "</h2><div ><table class='table_360'>\n" +
                "<thead>\n" +
                "<tr><th colspan=\"6\">مشخصات همکار</th></tr>    <tr><th>\n" +
                "نام</th>\n" +
                "<th>\n" +
                "نام خانوادگي</th>\n" +
                "<th>\n" +
                "کد پرسنلي</th>\n" +
                "<th>\n" +
                "واحد</th>\n" +
                "<th>\n" +
                "بخش</th>\n" +
                "<th>\n" +
                "سمت</th>\n" +
                "</tr></thead><tr><td>" + firstName + "</td><td>" + lastName + "</td><td>" + personnelCode +
                "</td><td>" + unit + "</td><td>" + section + "</td><td>" + position + "</td></tr></table></div><div ><table class='table_360'>\n" +
                "<thead>\n" +
                "<tr><th colspan=\"5\" >امتياز کسب شده در هريک از شاخص\u200Cهاي ارزيابي</th></tr>    <tr><th>\n" +
                "رديف\n" +
                "</th>\n" +
                "<th>\n" +
                "شاخص هاي ارزيابي\n" +
                "</th>\n" +
                "<th>\n" +
                "امتیاز ارزیابی سایرین\n" +
                "</th>\n" +
                "<th>\n" +
                "امتیاز خود ارزیابی\n" +
                "</th>\n" +
                "<th>\n" +
                "ميانگين امتياز در بخش</th>\n" +
                "</tr></thead>";

        List<KPIScoreInfo> kpisScoreInfo = karname.getKpisScoreInfo();

        List<String> allKPIs = findAllKPIs();

        for (String kpiCode : allKPIs) {
            KPIScoreInfo kpiScoreInfo = null;
            for (KPIScoreInfo scoreInfo : kpisScoreInfo) {
                if (scoreInfo.getKpiCode().equals(kpiCode)) {
                    kpiScoreInfo = scoreInfo;
                }
            }
            if (kpiScoreInfo != null) {
                String index = kpiScoreInfo.getKpiCode();
                String kpiName = assertNullity(kpiScoreInfo.getKpiName());
                String previousScore = assertNullity(kpiScoreInfo.getPreviousScore());
                String score = assertNullity(kpiScoreInfo.getScore());
                String scoreSelf = assertNullity(kpiScoreInfo.getScoreSelf());
                String averageScore = assertNullity(kpiScoreInfo.getAverageScore());
                newFile += ("<tr>\n" +
                        "<td>" + index + "</td>\n" +
                        "<td>\n" + kpiName + "</td>\n" +
                        "<td>" + score + "</td>\n" +
                        "<td>" + scoreSelf + "</td>\n" +
                        "<td>" + averageScore + "</td>\n" +
                        "</tr>");
            } else {
                String index = kpiCode;
                String kpiName = findKPIValueByKPICode(kpiCode);
                String previousScore = "-";
                String score = "-";
                String scoreSelf = "-";
                String averageScore = "-";
                newFile += ("<tr>\n" +
                        "<td>" + index + "</td>\n" +
                        "<td>\n" + kpiName + "</td>\n" +
                        "<td>" + score + "</td>\n" +
                        "<td>" + scoreSelf + "</td>\n" +
                        "<td>" + averageScore + "</td>\n" +
                        "</tr>");
            }
        }

        String totalScore = assertNullity(karname.getTotalScoreInfo().getTotalScore());
        totalScore = totalScore.equals("0") ? "-" : totalScore;
        String totalSelfScore = assertNullity(karname.getTotalScoreInfo().getTotalSelfScore());
        totalSelfScore = totalSelfScore.equals("0") ? "-" : totalSelfScore;
        String totalIncompleteForms = assertNullity(karname.getTotalScoreInfo().getIncompleteFormsCount());

        newFile += ("</table></div><div ><table class='table_360'>\n" +
                "<thead>\n" +
                "<th>\n" + "امتياز کلي ارزيابي</th>\n" +
                "<th>\n" + "امتياز کلي خود ارزيابي</th>\n" +
                "<th>\n" + "تعداد فرم\u200Cهاي ارزيابي تکميل نشده توسط همکار</th>\n" +
                "</thead>" +

                "<tr>" +
                "<td>" + totalScore + "</td>" +
                "<td>" + totalSelfScore + "</td>" +
                "<td>" + totalIncompleteForms + "</td>" +
                "</tr></table></div><div ><table class='table_360'><thead>\n" +
                "<tr><th colspan=\"4\">امتياز کسب شده کلي به تفکيک هر گروه از ذينفعان</th></tr>    <tr><th>\n" +
                "رديف\n" +
                "</th>\n" +
                "<th>\n" +
                " گروه\u200Cهاي ذينفع </th>\n" +
                "<th>\n" +
                "امتياز کسب شده\n" +
                "</th>\n" +
                "<th>\n" +
                "ميانگين امتياز در بخش</th>\n" +
                "</tr></thead>");

        Map<String, BigDecimal> categoryTotalScore = modirKarname.getTotalScore_step();
        int index = 1;


        for (String category : allCategories) {
            BigDecimal catTotalScore = categoryTotalScore.get(category);
            newFile += ("<tr>\n" +
                    "<td>" + index++ + "</td>\n" +
                    "<td>\n" + categoryMapping.get(category) + "</td>\n" +
                    "<td>" + assertNullity(catTotalScore) + "</td>\n" +
                    "<td>" + assertNullity(modirKarname.getCategoryAverage().get(category)) + "</td>\n" +
                    "</tr>");
        }


        newFile += (
                "</table></div><div ><table class='table_360'>\n" +
                        "<thead> <tr><th colspan='7'>امتياز کسب شده به تفکيک ذينفعان در هر شاخص</th> </tr>" +
                        "<tr><th colspan='2'></th>");

        Map<String, Map<String, BigDecimal>> kpiMianginVazni_step = modirKarname.getKpiMianginVazni_step();
        for (String category : kpiMianginVazni_step.keySet()) {
            newFile += ("<th colspan='1'>" + categoryMapping.get(category) + "</th>");
        }
        newFile += (
                "</tr><tr><th>\n" +
                        "رديف\n" +
                        "</th>\n" +
                        "<th>\n" +
                        "شاخص\u200Cهاي ارزيابي</th>\n" +
                        "<th>\n" +
                        "</th>\n" +
                        "<th>\n" +
                        "</th>\n" +
                        "<th>\n" +
                        "</th>\n" +
                        "<th>\n" +
                        "</th>\n" +
                        "<th>\n" +
                        "</th></tr><thead>\n");
        index = 0;
        Map<String, Map<String, String>> traversedKpiMianginVazni_step = getKpiMianginVazniStepTraversed(kpiMianginVazni_step);

        for (String kpiName : traversedKpiMianginVazni_step.keySet()) {
            Map<String, String> categoryMianginVazni = traversedKpiMianginVazni_step.get(kpiName);
            index++;
            newFile += ("<tr><td>" + index + "</td>\n" +
                    "<td>" + kpiName + "</td>");
            for (String category : categoryMianginVazni.keySet()) {
                String score = categoryMianginVazni.get(category);
                newFile += (
                        "<td>" + score + "</td>\n");
            }
            newFile += ("</tr>\n");

        }

        newFile += (
                "</table></div>" +
                        "<p>\n" +
                        "نکات قابل توجه:\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "۱. حداکثر امتياز قابل کسب توسط همکاران، عدد 100 مي\u200Cباشد.\n" +
                        "<br/>\n" +
                        "۲. تعداد فرم\u200Cهايي که فرد اقدام به تکميل آن براي ساير همکاران ننموده\u200Cاست در قسمت \" تعداد فرم\u200Cهاي ارزيابي تکميل نشده توسط همکار\" درج شده\u200Cاست.\n" +
                        "<br/>\n" +
                        "۳. ميانگين امتياز شاخص\u200Cها با \"امتياز کلي ارزيابي\" به دليل اعمال ضرايب در شاخص\u200Cها، اندکي متفاوت مي\u200Cباشد.\n" +
                        "</p></body>\n" +
                        "</html>");

        buffer.write(newFile.getBytes("utf-8"));

        return buffer.toByteArray();
    }

    private static void fillSectionCategoryAverageScore(List<ModirKarname> modirKarnames) {
        Map<String, Map<String, BigDecimal>> categorySectionAverage = new LinkedHashMap<String, Map<String, BigDecimal>>();
        Map<String, Map<String, BigDecimal>> categorySectionAverageSum = new LinkedHashMap<String, Map<String, BigDecimal>>();
        Map<String, Map<String, BigDecimal>> categorySectionAverageCount = new LinkedHashMap<String, Map<String, BigDecimal>>();
        for (String category : allCategories) {
            categorySectionAverage.put(category, new LinkedHashMap<String, BigDecimal>());
            categorySectionAverageSum.put(category, new LinkedHashMap<String, BigDecimal>());
            categorySectionAverageCount.put(category, new LinkedHashMap<String, BigDecimal>());
        }
        for (ModirKarname modirKarname : modirKarnames) {
            SelfKarname karname = modirKarname.getSelfKarname();
            Map<String, BigDecimal> categoryTotalScore = modirKarname.getTotalScore_step();
            for (String category : categoryTotalScore.keySet()) {
                BigDecimal catScore = categoryTotalScore.get(category);
                if (categorySectionAverageSum.containsKey(category)) {
                    Map<String, BigDecimal> sectionAverageSum = categorySectionAverageSum.get(category);
                    Map<String, BigDecimal> sectionAverageCount = categorySectionAverageCount.get(category);
                    if (sectionAverageSum.containsKey(karname.getUserInfo().getSection())) {
                        BigDecimal score = sectionAverageSum.get(karname.getUserInfo().getSection());
                        BigDecimal count = sectionAverageCount.get(karname.getUserInfo().getSection());
                        sectionAverageSum.put(karname.getUserInfo().getSection(), score.add(catScore));
                        sectionAverageCount.put(karname.getUserInfo().getSection(), count.add(BigDecimal.ONE));
                    } else {
                        sectionAverageSum.put(karname.getUserInfo().getSection(), catScore);
                        sectionAverageCount.put(karname.getUserInfo().getSection(), BigDecimal.ONE);
                    }
                } else {
                    Map<String, BigDecimal> sectionAverageSum = new LinkedHashMap<String, BigDecimal>();
                    Map<String, BigDecimal> sectionAverageCount = new LinkedHashMap<String, BigDecimal>();
                    categorySectionAverageSum.put(category, sectionAverageSum);
                    categorySectionAverageCount.put(category, sectionAverageCount);
                    sectionAverageSum.put(karname.getUserInfo().getSection(), catScore);
                    sectionAverageCount.put(karname.getUserInfo().getSection(), BigDecimal.ONE);
                }
            }
        }


        for (String category : categorySectionAverageSum.keySet()) {
            HashMap<String, BigDecimal> sectionAverage = new LinkedHashMap<String, BigDecimal>();
            Map<String, BigDecimal> sectionAverageSum = categorySectionAverageSum.get(category);
            for (String sectionName : sectionAverageSum.keySet()) {
                sectionAverage.put(sectionName, sectionAverageSum.get(sectionName).divide(categorySectionAverageCount.get(category).get(sectionName), RoundingMode.FLOOR));
            }
            categorySectionAverage.put(category, sectionAverage);
        }


        for (ModirKarname modirKarname : modirKarnames) {
            SelfKarname karname = modirKarname.getSelfKarname();
            String section = karname.getUserInfo().getSection();
            for (String category : modirKarname.getCategoryAverage().keySet()) {
                Map<String, BigDecimal> sectionsAverage = categorySectionAverage.get(category);
                modirKarname.getCategoryAverage().put(category, assertNullity(sectionsAverage.get(section)) + "");
            }
        }
    }


    private static Map<String, Map<String, String>> getKpiMianginVazniStepTraversed(Map<String, Map<String, BigDecimal>> kpiMianginVazni_step) throws SQLException {
        Map<String, Map<String, String>> traversedMap = new LinkedHashMap<String, Map<String, String>>();
        List<String> allKPIs = findAllKPIs();
        for (String kpiCode : allKPIs) {
            HashMap<String, String> categoryScore = new LinkedHashMap<String, String>();
            for (String category : allCategories) {
                categoryScore.put(category, "-");
            }
            traversedMap.put(findKPIValueByKPICode(kpiCode), categoryScore);
        }
        for (String category : kpiMianginVazni_step.keySet()) {
            Map<String, BigDecimal> kpiMianginVazni = kpiMianginVazni_step.get(category);
            for (String kpiCode : kpiMianginVazni.keySet()) {
                BigDecimal score = kpiMianginVazni.get(kpiCode);
                String kpiName = findKPIValueByKPICode(kpiCode);
                Map<String, String> innerTraversedMap = traversedMap.get(kpiName);
                innerTraversedMap.put(category, score + "");
            }
        }
        return traversedMap;
    }

    private static SelfKarname generateSelfKarname(String userId, Map<String, Map<String, BigDecimal>> kpiMianginVazniPerCategory, Map<String, BigDecimal> totalScorePerCategory) throws SQLException {
        Map<String, BigDecimal> kpiMianginVazniTotal = new LinkedHashMap<String, BigDecimal>();
        Map<String, BigDecimal> kpiMianginVazniTotalSelf = new LinkedHashMap<String, BigDecimal>();
        List<String> kpiCodes = findAllKPIs();
        for (String kpiCode : kpiCodes) {
            BigDecimal kpiTotalScore = BigDecimal.ZERO;
            BigDecimal kpiTotalScoreSelf = BigDecimal.ZERO;
            int categoryCount = 0;
            int categoryCountSelf = 0;
            for (String category : kpiMianginVazniPerCategory.keySet()) {
                Map<String, BigDecimal> kpiMianginVazni = kpiMianginVazniPerCategory.get(category);
                BigDecimal kpiScorePerCategory = kpiMianginVazni.get(kpiCode);
                if (kpiScorePerCategory != null) {
                    if (!category.equals(CATEGORY_SELF)) {
                        kpiTotalScore = kpiTotalScore.add(kpiScorePerCategory);
                        categoryCount++;
                    } else {
                        kpiTotalScoreSelf = kpiTotalScoreSelf.add(kpiScorePerCategory);
                        categoryCountSelf++;
                    }
                }
            }
            if (categoryCount != 0) {
                kpiMianginVazniTotal.put(kpiCode, kpiTotalScore.divide(new BigDecimal(categoryCount), RoundingMode.FLOOR).setScale(6, BigDecimal.ROUND_CEILING));
            }
            if (categoryCountSelf != 0) {
                kpiMianginVazniTotalSelf.put(kpiCode, kpiTotalScoreSelf.divide(new BigDecimal(categoryCountSelf), RoundingMode.FLOOR).setScale(6, BigDecimal.ROUND_CEILING));
            }
        }
        BigDecimal sumScore = BigDecimal.ZERO;
        int categoryCount = 0;
        BigDecimal sumScoreSelf = BigDecimal.ZERO;
        int categoryCountSelf = 0;
        for (String category : totalScorePerCategory.keySet()) {
            BigDecimal totalScoreCat = totalScorePerCategory.get(category);
            if (totalScoreCat != null) {
                if (!category.equals(CATEGORY_SELF)) {
                    sumScore = sumScore.add(totalScoreCat);
                    categoryCount++;
                } else {
                    sumScoreSelf = sumScoreSelf.add(totalScoreCat);
                    categoryCountSelf++;
                }
            }
        }
        BigDecimal totalScore = BigDecimal.ZERO;
        if (categoryCount != 0) {
            totalScore = sumScore.divide(new BigDecimal(categoryCount), RoundingMode.FLOOR).setScale(6, BigDecimal.ROUND_CEILING);
        }
        BigDecimal totalScoreSelf = BigDecimal.ZERO;
        if (categoryCountSelf != 0) {
            totalScoreSelf = sumScoreSelf.divide(new BigDecimal(categoryCountSelf), RoundingMode.FLOOR).setScale(6, BigDecimal.ROUND_CEILING);
        }

        UserInfo userInfo = findUserInfoByUserId(userId);
        List<KPIScoreInfo> kpisScoreInfo = new ArrayList<KPIScoreInfo>();
        int index = 1;
        for (String kpiCode : kpiCodes) {
            BigDecimal kpiTotal = kpiMianginVazniTotal.get(kpiCode);
            BigDecimal kpiTotalSelf = kpiMianginVazniTotalSelf.get(kpiCode);
            if (kpiTotal != null || kpiTotalSelf != null) {
                KPIScoreInfo kpiScoreInfo = new KPIScoreInfo(index++ + "", findKPIValueByKPICode(kpiCode), ""
                        , kpiTotal == null ? "-" : kpiTotal.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_CEILING) + ""
                        , kpiTotalSelf == null ? "-" : kpiTotalSelf.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_CEILING) + ""
                        , "", kpiCode);
                kpisScoreInfo.add(kpiScoreInfo);
            }
        }
        String incompleteCount = findIncompleteFormsCount(userId);
        TotalScoreInfo totalScoreInfo = new TotalScoreInfo(totalScore.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_CEILING) + "", totalScoreSelf.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_CEILING) + "", "", incompleteCount);
        return new SelfKarname(userInfo, kpisScoreInfo, totalScoreInfo);
    }

    private static String findIncompleteFormsCount(String userId) throws SQLException {
        String queryStr = "select formcnt from cal_incomplete_forms where userId = ?";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        statement.setString(1, userId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return "";
    }

    private static Map<String, BigDecimal> calculateTotalGradePerUserPerCategory(String userId, Map<String, Map<String, BigDecimal>> kpiMianginVazniPerCategory) throws SQLException {
        Map<String, BigDecimal> totalScorePerCategory = new LinkedHashMap<String, BigDecimal>();

        for (String category : kpiMianginVazniPerCategory.keySet()) {
            Map<String, BigDecimal> kpiMianginVazni = kpiMianginVazniPerCategory.get(category);
            String assesseeRoleCode = findRoleCodeByUserId(userId);
            BigDecimal totalKPIScoreSoorat = BigDecimal.ZERO;
            BigDecimal totalKPIScoreMakhraj = BigDecimal.ZERO;
            for (String kpiCode : kpiMianginVazni.keySet()) {
                Integer assesseeKPIFactor = findAssesseeKPIFactor(assesseeRoleCode, kpiCode);
                totalKPIScoreSoorat = totalKPIScoreSoorat.add(kpiMianginVazni.get(kpiCode).multiply(new BigDecimal(assesseeKPIFactor)));
                totalKPIScoreMakhraj = totalKPIScoreMakhraj.add(new BigDecimal(assesseeKPIFactor));
            }
            if (totalKPIScoreMakhraj.compareTo(BigDecimal.ZERO) != 0) {
                totalScorePerCategory.put(category, totalKPIScoreSoorat.divide(totalKPIScoreMakhraj, RoundingMode.FLOOR));
            }
        }
        return totalScorePerCategory;
    }

    private static Map<String, Map<String, BigDecimal>> calculateTotalGradePerUserPerKPIPerCategory(Map<String, Map<String, BigDecimal>> questionMianginVazniPerCategory) throws SQLException {
        Map<String, Map<String, BigDecimal>> kpiMianginVazniPerCategory = new LinkedHashMap<String, Map<String, BigDecimal>>();
        for (String category : allCategories) {
            kpiMianginVazniPerCategory.put(category, new LinkedHashMap<String, BigDecimal>());
        }

        for (String category : questionMianginVazniPerCategory.keySet()) {
            Map<String, BigDecimal> questionMianginVazni = questionMianginVazniPerCategory.get(category);
            Map<String, BigDecimal> kpiMianginVazni = new LinkedHashMap<String, BigDecimal>();
            List<String> kpiCodes = findAllKPIs();

            for (String kpiCode : kpiCodes) {
                BigDecimal totalScoreSoorat = BigDecimal.ZERO;
                BigDecimal totalScoreMakhraj = BigDecimal.ZERO;
                for (String questionId : questionMianginVazni.keySet()) {
                    Integer questionKPIFactor = findQuestionKPIFactor(questionId, kpiCode);
                    BigDecimal soorat = questionMianginVazni.get(questionId).multiply(new BigDecimal(questionKPIFactor));
                    totalScoreSoorat = totalScoreSoorat.add(soorat);
                    if (!soorat.equals(BigDecimal.ZERO)) {
                        totalScoreMakhraj = totalScoreMakhraj.add(new BigDecimal(questionKPIFactor));
                    }
                }
                if (totalScoreMakhraj.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal totalKPIScore = totalScoreSoorat.divide(totalScoreMakhraj, RoundingMode.FLOOR);
                    kpiMianginVazni.put(kpiCode, totalKPIScore);
                }
            }
            kpiMianginVazniPerCategory.put(category, kpiMianginVazni);
        }
        return kpiMianginVazniPerCategory;
    }

    private static Map<String, Map<String, BigDecimal>> caculateQuestionGradesPerUserPerCategory(String step, String userId) throws SQLException {
        ResultSet resultSet = calculateQuestionGradesPerPerson(step, userId);
        Map<String, Map<String, BigDecimal>> questionMianginVazniPerCategory = new LinkedHashMap<String, Map<String, BigDecimal>>();
        for (String category : allCategories) {
            questionMianginVazniPerCategory.put(category, new LinkedHashMap<String, BigDecimal>());
        }

        Map<String, Map<String, BigDecimal>> questionMianginVazniSooratPerCategory = new LinkedHashMap<String, Map<String, BigDecimal>>();
        for (String category : allCategories) {
            questionMianginVazniSooratPerCategory.put(category, new LinkedHashMap<String, BigDecimal>());
        }

        Map<String, Map<String, BigDecimal>> questionMianginVazniMakhrajPerCategory = new LinkedHashMap<String, Map<String, BigDecimal>>();
        for (String category : allCategories) {
            questionMianginVazniMakhrajPerCategory.put(category, new LinkedHashMap<String, BigDecimal>());
        }

        Map<String, BigDecimal> questionMianginVazni;
        Map<String, BigDecimal> questionMianginVazniSoorat;
        Map<String, BigDecimal> questionMianginVazniMakhraj;
        String assesseeRoleCode = findRoleCodeByUserId(userId);
        while (resultSet.next()) {
            String assessorId = resultSet.getString(1);
            if (!assessorId.equals("")) {
                String assessorRoleCode = findRoleCodeByUserId(assessorId);

                String category = findAssessorCategory(step, userId, assessorId);

                questionMianginVazniSoorat = questionMianginVazniSooratPerCategory.get(category);
                questionMianginVazniMakhraj = questionMianginVazniMakhrajPerCategory.get(category);

                String questionId = resultSet.getString(2);
                BigDecimal score = new BigDecimal(resultSet.getDouble(3));
                Integer assesseeAssessorFactor = findAssesseeAssessorFactor(assesseeRoleCode, assessorRoleCode);
                Integer questionAssesseeFactor = findQuestionAssesseeFactor(questionId, assesseeRoleCode);

                BigDecimal weightedScore = score.multiply(new BigDecimal(assesseeAssessorFactor)).multiply(new BigDecimal(questionAssesseeFactor));
                if (questionMianginVazniSoorat.containsKey(questionId)) {
                    BigDecimal previousScoreSoorat = questionMianginVazniSoorat.get(questionId);
                    BigDecimal previousScoreMakhraj = questionMianginVazniMakhraj.get(questionId);
                    questionMianginVazniSoorat.put(questionId, previousScoreSoorat.add(weightedScore));
                    if (!weightedScore.equals(BigDecimal.ZERO)) {
                        BigDecimal makhraj = new BigDecimal(assesseeAssessorFactor).multiply(new BigDecimal(questionAssesseeFactor));
                        questionMianginVazniMakhraj.put(questionId, previousScoreMakhraj.add(makhraj));
                    }
                } else {
                    questionMianginVazniSoorat.put(questionId, weightedScore);
                    if (!weightedScore.equals(BigDecimal.ZERO)) {
                        BigDecimal makhraj = new BigDecimal(assesseeAssessorFactor).multiply(new BigDecimal(questionAssesseeFactor));
                        questionMianginVazniMakhraj.put(questionId, makhraj);
                    } else {
                        questionMianginVazniMakhraj.put(questionId, BigDecimal.ZERO);
                    }
                }
            }
        }
        for (String category : questionMianginVazniSooratPerCategory.keySet()) {
            questionMianginVazni = questionMianginVazniPerCategory.get(category);
            questionMianginVazniSoorat = questionMianginVazniSooratPerCategory.get(category);
            questionMianginVazniMakhraj = questionMianginVazniMakhrajPerCategory.get(category);
            for (String questionId : questionMianginVazniSoorat.keySet()) {
                if (!questionMianginVazniMakhraj.get(questionId).equals(BigDecimal.ZERO)) {
                    questionMianginVazni.put(questionId, questionMianginVazniSoorat.get(questionId).divide(questionMianginVazniMakhraj.get(questionId), RoundingMode.FLOOR));
                } else {
                    questionMianginVazni.put(questionId, BigDecimal.ZERO);
                }
            }
        }
        return questionMianginVazniPerCategory;
    }

    private static String findAssessorCategory(String step, String userId, String assessorId) throws SQLException {
        String assesseeCategoryId = findUserCategory(step, userId);
        String assessorCategoryId = findUserCategory(step, assessorId);
        String assesseeManagerRoleCode = findManagerRoleCode(userId);
        String assessorManagerRoleCode = findManagerRoleCode(assessorId);
        String assesseeIndirectManagerRoleCode = findIndirectManagerRoleCode(userId);

        String assesseeRoleCode = findRoleCodeByUserId(userId);
        String assessorRoleCode = findRoleCodeByUserId(assessorId);

        String exceptionalRelation = isRelationExceptional(assesseeRoleCode, assessorRoleCode);
        if (exceptionalRelation != null) {
            return exceptionalRelation;
        }

        if (findRoleCodeByUserId(assessorId).equals(assesseeIndirectManagerRoleCode)) {
            return CATEGORY_MANAGER;
        }
        if (findRoleCodeByUserId(assessorId).equals(assesseeManagerRoleCode)) {
            return CATEGORY_MANAGER;
        }
        if (findRoleCodeByUserId(userId).equals(assessorManagerRoleCode)) {
            return CATEGORY_SUBSET;
        }
        if (userId.equals(assessorId)) {
            return CATEGORY_SELF;
        }
        if (assesseeCategoryId.equals(assessorCategoryId)) {
            return CATEGORY_COLLEAGUE;
        }
        return CATEGORY_OUT;
    }

    private static String isRelationExceptional(String assesseeRoleCode, String assessorRoleCode) throws SQLException {
        String queryStr = "select relation from cal_relation_exception where assesseeCode = ? and assessorCode = ?";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        statement.setString(1, assesseeRoleCode);
        statement.setString(2, assessorRoleCode);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }

    private static String findIndirectManagerRoleCode(String assessorId) throws SQLException {
        String queryStr = "select um2.managerRCode from cal_userrcode_managerrcode um\n" +
                "inner join cal_userrcode_managerrcode um2 on um.managerRCode = um2.userRCode\n" +
                "inner join cal_user_role ur on ur.roleCode = um.userRCode\n" +
                "where ur.userId = ?";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        statement.setString(1, assessorId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }

    private static ResultSet calculateQuestionGradesPerPerson(String step, String userId) throws SQLException {
        String queryStr = "select u.idnumber assessorId, que.id questionId, qas.fraction grade\n" +
                "from mdl_quiz q\n" +
                "inner join mdl_course c on q.course = c.id and c.idnumber = '" + step + "*" + userId + "'\n" +
                "inner join mdl_quiz_attempts qat on qat.quiz = q.id\n" +
                "inner join mdl_question_usages quba on qat.uniqueid = quba.id\n" +
                "LEFT JOIN mdl_question_attempts qa ON qa.questionusageid = quba.id\n" +
                "inner join mdl_question que on qa.questionid = que.id\n" +
                "LEFT JOIN mdl_question_attempt_steps qas ON qas.questionattemptid = qa.id and qas.fraction is not null\n" +
                "inner join mdl_user u on qas.userid = u.id\n" +
//                "LEFT JOIN mdl_question_attempt_step_data qasd ON qasd.attemptstepid = qas.id\n";
                "LEFT JOIN mdl_question_attempt_step_data qasd ON qasd.attemptstepid = qas.id\n" +
                "where concat(q.id, '#', u.idnumber) not IN\n" +
                "(\n" +
                "    SELECT concat(quizId, '#', substr(assessorID, 3)) FROM cal_quiz_incorrect\n" +
                ")";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        return statement.executeQuery();
    }

    private static String findRoleCodeByUserId(String userId) throws SQLException {
        String queryStr = "select roleCode from cal_user_role where userId = ?";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        statement.setString(1, userId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }

    private static Integer findAssesseeAssessorFactor(String assesseeRoleCode, String assessorRoleCode) throws SQLException {
        String queryStr = "select factor from cal_asseercode_assorrcode_factor where assesseeRCode = ? and assessorRCode = ?";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        statement.setString(1, assesseeRoleCode);
        statement.setString(2, assessorRoleCode);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return Integer.parseInt(resultSet.getString(1));
        }
        return 3;
    }

    private static Integer findQuestionAssesseeFactor(String questionCode, String assesseeRoleCode) throws SQLException {
        String queryStr = "select factor from cal_quescode_asseercode_factor where questionCode = ? and assesseeRCode = ?";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        statement.setString(1, questionCode);
        statement.setString(2, assesseeRoleCode);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return Integer.parseInt(resultSet.getString(1));
        }
        return 0;
    }

    private static Integer findQuestionKPIFactor(String questionCode, String kpiCode) throws SQLException {
        String queryStr = "select factor from cal_quescode_kpicode_factor where quesCode = ? and kpiCode = ?";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        statement.setString(1, questionCode);
        statement.setString(2, kpiCode);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return Integer.parseInt(resultSet.getString(1));
        }
        return 0;
    }

    private static Integer findAssesseeKPIFactor(String assesseeRoleCode, String kpiCode) throws SQLException {
        String queryStr = "select factor from cal_asseercode_kpicode_factor where assesseeRCode = ? and kpiCode = ?";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        statement.setString(1, assesseeRoleCode);
        statement.setString(2, kpiCode);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return Integer.parseInt(resultSet.getString(1));
        }
        return 0;
    }

    private static List<String> findAllKPIs() throws SQLException {
        List<String> kpiCodes = new ArrayList<String>();
        String queryStr = "select kpicode from cal_kpi_info order by kpiCode";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            kpiCodes.add(resultSet.getString(1));
        }
        return kpiCodes;
    }

    private static String findKPIValueByKPICode(String kpiCode) throws SQLException {
        String queryStr = "select kpivalue from cal_kpi_info where kpiCode = ?";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        statement.setString(1, kpiCode);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }

    private static List<String> findAllUserIds() throws SQLException {
        List<String> userIds = new ArrayList<String>();
        String queryStr = "select distinct(userId) from cal_user_role";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            userIds.add(resultSet.getString(1));
        }
        return userIds;
    }

    private static UserInfo findUserInfoByUserId(String userId) throws SQLException {
        String queryStr = "select icq, skype, msn, phone1, case when phone1 = '' then yahoo when phone1 = '-' then yahoo when phone1 = '_' then yahoo when phone1 is null then yahoo else concat(yahoo, ' ', phone1) end as position, username from mdl_user where idnumber = ?";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        statement.setString(1, userId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return new UserInfo(resultSet.getString(1), resultSet.getString(2),
                    userId, resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6));
        }
        return null;
    }

    private static String findUserCategory(String step, String userId) throws SQLException {
        String queryStr = "select category from mdl_course where idnumber = '" + step + "*" + userId + "'";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }

    private static String findManagerRoleCode(String userId) throws SQLException {
        String queryStr = "select managerRCode from cal_userrcode_managerrcode where userRCode = ?";
        PreparedStatement statement = SQLConnector.conn.prepareStatement(queryStr);
        statement.setString(1, findRoleCodeByUserId(userId));
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }
}
