package tk.mybatis.springboot;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import tk.mybatis.springboot.util.CellUtil;

import java.io.*;

/**
 * Created by ltao on 2017/8/7.
 */
public class ExcelTest {

    @Test
    public void testCell() throws Exception {
        String type = "renwokan";
//        File file = new File(InspectionConstants.PATHMAP.get(type));
        File file = new File("");
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
//        File file = new File(InspectionConstants.PATHMAP.get(type));
        File file = new File("");
        InputStream is = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(is);
//        System.out.println(workbook.getNumberOfSheets());
//        System.out.println(Thread.currentThread().getStackTrace()[0].getMethodName());
//        System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
//        System.out.println(Thread.currentThread().getStackTrace()[1].getClassName());
//        System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber());
//        System.out.println(Thread.currentThread().getStackTrace()[1].getFileName());
//        XSSFSheet sheet = workbook.getSheet("交易金额");
//        XSSFCellStyle cellStyle = sheet.getRow(4).getCell(0).getCellStyle();
        XSSFSheet sheet = workbook.getSheet("");
//        System.out.println(sheet.getRow(5).getCell(0).toString());
//        System.out.println(sheet.getRow(5).getCell(0).getCellStyle());
        System.out.println(sheet.getRow(3).getCell(3).toString());
        System.out.println(sheet.getRow(4).getCell(3).toString());
        System.out.println(sheet.getRow(4).getCell(3).isPartOfArrayFormulaGroup());
        System.out.println(sheet.getRow(4).getCell(3).getCellType());
        sheet.getRow(4).getCell(3).setCellFormula(null);
        System.out.println(sheet.getRow(4).getCell(3).getCellType());
        System.out.println(sheet.getRow(4).getCell(3).isPartOfArrayFormulaGroup());
        if (sheet.getRow(4).getCell(3) == null) {
            System.out.println("null");
        }
        System.out.println(sheet.getLastRowNum());
        System.out.println("end");
    }

    @Test
    public void test3() throws Exception {
        File f = new File("D:\\test.xls");

        String type = "totalBusinessVolume";
        InputStream is = new FileInputStream(f);
//        XSSFWorkbook workbook = new XSSFWorkbook(is);

//        XSSFSheet sheet = workbook.getSheet("交易金额");
//        XSSFRow row = sheet.createRow(10);
//        for (int i = 0; i < 200; i++) {
//            XSSFCellStyle cellStyle = workbook.createCellStyle();
//            cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
//            cellStyle.setFillForegroundColor((short) i);
//            row.createCell(i).setCellStyle(cellStyle);
//        }

        Workbook workbook1 = new HSSFWorkbook(is);
        Sheet sheet1 = workbook1.getSheetAt(0);
        System.out.println(sheet1.getRow(0).getCell(0));
        sheet1.getRow(6).createCell(1).setCellValue("测试测试aaa");
        sheet1.getRow(6).createCell(2).setCellValue("Ø Ø Ø Ø ");
        sheet1.getRow(6).createCell(3).setCellValue("\\Ø \\Ø \\Ø \\Ø ");
//        sheet1.createRow(7);
//        System.out.println(sheet1.getRow(2).getCell(2).toString());
//        System.out.println(sheet1.getRow(3).getCell(2).toString());
//        System.out.println(sheet1.getRow(3).getCell(1).getCellType());
//        System.out.println(sheet1.getRow(3).getCell(2).getCellType());
//        System.out.println(sheet1.getRow(4).getCell(0).getCellType());
//        System.out.println(sheet1.getRow(3).getCell(2).getCellFormula());
//        sheet1.getRow(3).getCell(2).setCellFormula(null);
//        System.out.println(sheet1.getRow(3).getCell(2).getCellType());
        System.out.println("delete row");
        System.out.println(sheet1.getLastRowNum());
//        sheet1.shiftRows(65534, 100000000, -5);
        removeRows(sheet1, 8, 2);
        Row row = sheet1.getRow(2);
        System.out.println("middle");
//        sheet1.removeRow(row);
        System.out.println(row.getCell(0));

        System.out.println("end");
        File file1 = new File("D:\\test1.xls");


        /**
         * xlsx -> xls :文件可以打开，但提示错误
         * xls -> xlsx :文件不能打开
         * 结论：读取到哪种格式，就用哪种格式输出
         */
        OutputStream os = new FileOutputStream(file1);
        workbook1.write(os);


//        Workbook workbook2 = new XSSFWorkbook(is);
//        Sheet sheet2 = workbook2.getSheet("交易金额");
//        System.out.println(sheet2.getRow(3).getCell(2));

//        workbook.write(os);
        os.flush();
        os.close();
    }

    public static void removeRows(Sheet sheet, int start, int totalLines) {
        if (totalLines <= 0) return;
        int lastRowNum = sheet.getLastRowNum();
        if (start > lastRowNum) return;
        int end;
        if (lastRowNum - (totalLines * 2 - 1) >= start) {
            end = lastRowNum;
        } else {
            end = lastRowNum + totalLines - 1;
        }
        sheet.shiftRows(start + totalLines, end, -totalLines);
        if (end > 65535) {
            int lastRow = end - 65535;
            int lastStart = start + totalLines - lastRow;
            Row row;
            for (int i = 0; i < lastRow; i++) {
                row = sheet.getRow(lastStart + i);
                if (row != null) {
                    sheet.removeRow(row);
                }
            }
        }
    }

    @Test
    public void test4() {
        String path = this.getClass().getResource("/").getPath();
        System.out.println(path);
        for (String a : new File(path).list()) {
            System.out.println(a.indexOf("kk"));
        }

//        String fileName = "D:\\附录6：卓望巡检日报_20170706.xlsx";
//        System.out.println(fileName.lastIndexOf("."));
//        System.out.println(fileName.length());
//        System.out.println(fileName.substring(fileName.lastIndexOf(".")));
//        System.out.println(fileName.substring(fileName.lastIndexOf("."), fileName.length()));
    }


    /**
     * 测试使用sheet.removeRow(row)之后是否还能get到Row和Cell
     */
    @Test
    public void test5() throws Exception {
        File file = new File("D:\\test.xls");
        InputStream is = new FileInputStream(file);
        Workbook workbook = new HSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        CellUtil.clearRows(sheet, 0, 3);
        System.out.println("sheet.getRow(0) == null : " + (sheet.getRow(0) == null));
//        System.out.println("sheet.getRow(0).getCell(0) == null : " + sheet.getRow(0).getCell(0) == null);

        is.close();
        /**
         * 得出结论使用sheet.removeRow(row)之后会将 Row 置为 null；
         */
    }

    /**
     * 得出结论，路径不存在时，file不会为null，file.list()是null;
     */
    @Test
    public void test6() {
        File file = new File("G:\\logs\\logs\\");
        if (file == null) {
            System.out.println("null");
        } else if (file.list() == null) {
            System.out.println("file.list() is null");
        } else {
            System.out.println(file.list().length);
            for (String f : file.list()) {
                System.out.println(f);
            }
        }
    }
}
