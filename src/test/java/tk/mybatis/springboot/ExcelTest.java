package tk.mybatis.springboot;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import tk.mybatis.springboot.util.InspectionConstants;

import java.io.*;

/**
 * Created by ltao on 2017/8/7.
 */
public class ExcelTest {

    @Test
    public void testCell() throws Exception {
        String type = "renwokan";
        File file = new File(InspectionConstants.PATHMAP.get(type));
        InputStream is = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        XSSFSheet sheet = workbook.getSheetAt(1);
        Cell cell = null;
        for (int i = 0; i < 100; i++) {
            if (sheet.getRow(i) != null && sheet.getRow(i).getCell(0) != null
                    && "价格分类".equals(sheet.getRow(i).getCell(0).toString())) {
                cell = sheet.getRow(i).getCell(0);
                break;
            }
        }
        System.out.println(cell.getColumnIndex());
        System.out.println(cell.getRowIndex());
    }

    @Test
    public void test2() throws Exception {
        String type = "totalBusinessVolume";
        File file = new File(InspectionConstants.PATHMAP.get(type));
        InputStream is = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        System.out.println(workbook.getNumberOfSheets());
        System.out.println(Thread.currentThread().getStackTrace()[0].getMethodName());
        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
        System.out.println(Thread.currentThread().getStackTrace()[1].getClassName());
        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber());
        System.out.println(Thread.currentThread().getStackTrace()[1].getFileName());
        XSSFSheet sheet = workbook.getSheet("交易金额");
        XSSFCellStyle cellStyle = sheet.getRow(4).getCell(0).getCellStyle();
        System.out.println("");
    }

    @Test
    public void test3() throws Exception {
        File f = new File("D:\\test001.xlsx");
        OutputStream os = new FileOutputStream(f);
        String type = "totalBusinessVolume";
        File file = new File(InspectionConstants.PATHMAP.get(type));
        InputStream is = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        XSSFSheet sheet = workbook.getSheet("交易金额");
        XSSFRow row = sheet.createRow(10);
        for (int i = 0; i < 200; i++) {
            XSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
            cellStyle.setFillForegroundColor((short) i);
            row.createCell(i).setCellStyle(cellStyle);
        }
        workbook.write(os);
        os.flush();
        os.close();
    }

}
