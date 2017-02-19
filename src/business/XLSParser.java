package business;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 8/29/15
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class XLSParser {
    public static String getSheetName(String filePath) throws IOException {
        ArrayList<ArrayList<String>> parsedSheet = new ArrayList<ArrayList<String>>();
        FileInputStream file = new FileInputStream(new File(filePath));
        HSSFWorkbook workbook = new HSSFWorkbook(file);
        HSSFSheet sheet = workbook.getSheetAt(0);
        return sheet.getSheetName();
    }

    public static ArrayList<ArrayList<String>> parseInputFile(String filePath) throws IOException {
        ArrayList<ArrayList<String>> parsedSheet = new ArrayList<ArrayList<String>>();
        FileInputStream file = new FileInputStream(new File(filePath));
        HSSFWorkbook workbook = new HSSFWorkbook(file);
        HSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            ArrayList<String> parsedRow = new ArrayList<String>();
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    parsedRow.add(cell.toString());
                }
            }
            parsedSheet.add(parsedRow);
        }
        return parsedSheet;
    }
}
