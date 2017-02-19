import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class PlayGround {
    public static void main(String[] args) throws IOException {
//        ArrayList<Outlier> outliers = new ArrayList<Outlier>();
//        LineIterator iterator = IOUtils.lineIterator(new FileInputStream("Outliers.csv"), "UTF-8");
//        while (iterator.hasNext()) {
//            String line = iterator.next();
//            Outlier outlier = new Outlier(line.split("\u001B")[0], Integer.parseInt(line.split("\u001B")[1]));
//            outliers.add(outlier);
//        }
        String t = "/1/968/1679/1680";
        System.out.println(t.split("/").length);
        if (true) {
            return;
        }
        File mainDir = new File("scorecard10\\Shakhs");
/*
        Iterator<File> fileIterator = FileUtils.iterateFiles(mainDir, new String[]{"html"}, true);
        while (fileIterator.hasNext()) {
            File file = fileIterator.next();
            System.out.println(file.getName());
            LineIterator lineIterator = IOUtils.lineIterator(new FileInputStream(file), "UTF-8");
            StringBuffer content = new StringBuffer("");
            while (lineIterator.hasNext()) {
                String line = lineIterator.next();
                content = content.append(line).append("\n");
                if (line.equals("تعداد فرم‌هاي ارزيابي تکميل نشده توسط همکار</th>")) {
                    line = lineIterator.next();
                    String part1 = line.substring(0, StringUtils.ordinalIndexOf(line, "<td>", 2));
                    String part2 = line.substring(StringUtils.ordinalIndexOf(line, "<td>", 3));
                    String finalLine;
                    Outlier out = findOutiler(outliers, file.getName().substring(0, file.getName().length() - 5));
                    if (out != null) {
                        finalLine = part1 + "<td>" + out.getCount() + "</td>" + part2;
                    } else {
                        finalLine = part1 + "<td>0</td>" + part2;
                    }
                    content = content.append(finalLine).append("\n");
                }
            }
            IOUtils.write(content, new FileOutputStream(file), "UTF-8");
        }
*/
        File[] secondDirs = mainDir.listFiles();
        assert secondDirs != null;
        for (File secondDir : secondDirs) {
            File[] files = secondDir.listFiles();
            assert files != null;
            for (File file : files) {
                System.out.println(file.getName());
//                LineIterator lineIterator = IOUtils.lineIterator(new FileInputStream(file), "UTF-8");
//                while (lineIterator.hasNext()) {
//                    String line = lineIterator.next();
//                    if (line.equals("تعداد فرم‌هاي ارزيابي تکميل نشده توسط همکار</th>")) {
//                        line = lineIterator.next();
//                        String part1 = line.substring(0, StringUtils.ordinalIndexOf(line, "<td>", 2));
//                        String part2 = line.substring(StringUtils.ordinalIndexOf(line, "<td>", 3));
//                        String finalLine = part1 + "<td>100</td>" + part2;
//                    }
//                }
                String content = IOUtils.toString(new FileInputStream(file), "UTF-8");
//                content = content.replaceAll("0", "?");
//                content = content.replaceAll("1", "?");
//                content = content.replaceAll("2", "?");
//                content = content.replaceAll("3", "?");
//                content = content.replaceAll("4", "?");
//                content = content.replaceAll("5", "?");
//                content = content.replaceAll("6", "?");
//                content = content.replaceAll("7", "?");
//                content = content.replaceAll("8", "?");
//                content = content.replaceAll("9", "?");
//                content = content.replaceAll("colspan='?'", "colspan='1'");
//                content = content.replaceAll("colspan='?'", "colspan='2'");
//                content = content.replaceAll("colspan='?'", "colspan='3'");
//                content = content.replaceAll("colspan='?'", "colspan='4'");
//                content = content.replaceAll("colspan='?'", "colspan='5'");
//                content = content.replaceAll("colspan='?'", "colspan='6'");
//                content = content.replaceAll("colspan='?'", "colspan='7'");
//                content = content.replaceAll("colspan='?'", "colspan='8'");
//                content = content.replaceAll("colspan='?'", "colspan='9'");
//                content = content.replaceAll("colspan='??'", "colspan='10'");
//                content = content.replaceAll("colspan='??'", "colspan='11'");
//                content = content.replaceAll("colspan='??'", "colspan='12'");
//                content = content.replaceAll("colspan='??'", "colspan='13'");
//                content = content.replaceAll("colspan='??'", "colspan='14'");

//                content = content.replaceAll("colspan=\"?\"", "colspan='1'");
//                content = content.replaceAll("colspan=\"?\"", "colspan='2'");
//                content = content.replaceAll("colspan=\"?\"", "colspan='3'");
//                content = content.replaceAll("colspan=\"?\"", "colspan='4'");
//                content = content.replaceAll("colspan=\"?\"", "colspan='5'");
//                content = content.replaceAll("colspan=\"?\"", "colspan='6'");
//                content = content.replaceAll("colspan=\"?\"", "colspan='7'");
//                content = content.replaceAll("colspan=\"?\"", "colspan='8'");
//                content = content.replaceAll("colspan=\"?\"", "colspan='9'");
//                content = content.replaceAll("colspan=\"??\"", "colspan='10'");
//                content = content.replaceAll("colspan=\"??\"", "colspan='11'");
//                content = content.replaceAll("colspan=\"??\"", "colspan='12'");
//                content = content.replaceAll("colspan=\"??\"", "colspan='13'");
//                content = content.replaceAll("colspan=\"??\"", "colspan='14'");

//                content = content.replaceAll("h?", "h1");
//                content = content.replaceAll("h?", "h2");
//                content = content.replaceAll("h?", "h3");

//                content = content.replaceAll("table_???", "table_360");
//                content = content.replaceAll("UTF-?", "UTF-8");
                content = content.replaceAll("src=\"../../css/Dotin_Logo.png\"", "src=\"data:image/png;base64,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\"");


                IOUtils.write(content, new FileOutputStream(file), "UTF-8");
            }
        }


    }

    private static Outlier findOutiler(ArrayList<Outlier> outliers, String name) {
        for (Outlier outlier : outliers) {
            if (outlier.getUsername().equals(name)) {
                return outlier;
            }
        }
        return null;
    }

}

class Outlier {
    private String username;
    private int count;

    String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    int getCount() {
        return count;
    }

    void setCount(int count) {
        this.count = count;
    }

    Outlier(String username, int count) {
        this.username = username;
        this.count = count;
    }
}
