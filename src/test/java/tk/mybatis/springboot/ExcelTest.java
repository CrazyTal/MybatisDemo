package tk.mybatis.springboot;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import tk.mybatis.springboot.util.InspectionConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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
}
