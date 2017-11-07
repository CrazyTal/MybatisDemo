package tk.mybatis.springboot.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.springboot.model.Position;

/**
 * Excel单元格util
 */
public class CellUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CellUtil.class);

    /**
     * 获取列名
     *
     * @param cellCol
     * @return
     */
    public static String getCellVal(int cellCol) {
        String[] arr = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
                "T", "U", "V", "W", "X", "Y", "Z"};
        return getCellVal(cellCol, arr);
    }

    private static String getCellVal(int cellCol, String[] arr) {
        if (cellCol / arr.length > 0) {
            return getCellVal(cellCol / arr.length - 1, arr) + arr[cellCol % arr.length];
        } else {
            return arr[cellCol % arr.length];
        }
    }

    /**
     * 清空某列单元格公式
     *
     * @param sheet
     * @param start
     * @param col
     */
    public static void clearColFormula(Sheet sheet, Position start, int col) {
        Row row;
        int countRow = 0;
        while (countRow < InspectionConstants.SAFE_LINE) {
            row = sheet.getRow(start.getRow() + countRow);
            if (row == null || row.getCell(col) == null) {
                break;
            }
            row.getCell(col).setCellFormula(null);
            countRow++;
        }
    }

    /**
     * 清空rows
     *
     * @param sheet
     * @param start      起始行
     * @param totalLines 总行数(若总行数小于0，将会被替换为65535)
     */
    public static void clearRows(Sheet sheet, int start, int totalLines) {
        Row row;
        int emptyRows = 0;// 连续空行数目
        int countRow = 0;
        if (totalLines < 0) {
            totalLines = 65535;
        }
        while (countRow < totalLines) {
            row = sheet.getRow(start + countRow);
            if (row != null) {
                sheet.removeRow(row);
            } else {
                emptyRows++;
            }
            countRow++;
            if (emptyRows > 3)
                break;
        }
    }

    /**
     * 删除rows， 余下行向上移动
     *
     * @param sheet      sheet
     * @param start      起始行
     * @param totalLines 删除行数
     */
    public static void removeRows(Sheet sheet, int start, int totalLines) {
        if (totalLines <= 0)
            return;
        int lastRowNum = sheet.getLastRowNum();
        if (start > lastRowNum)
            return;
        int end;
        int excelMax = 65535;
        if (lastRowNum - (totalLines * 2 - 1) >= start) {
            end = lastRowNum;
        } else {
            end = lastRowNum + totalLines - 1;
        }
        sheet.shiftRows(start + totalLines, end, -totalLines);
        if (end > excelMax) {
            int lastRow = end - excelMax;
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

    /**
     * 设置单元格相乘公式
     *
     * @param cell cell
     * @param row1 cell1行
     * @param col1 cell1列
     * @param row2 cell2行
     * @param col2 cell2列
     */
    public static void setMultiplyFormula(Cell cell, int row1, int col1, int row2, int col2) {
        if (cell == null)
            return;
        cell.setCellFormula(CellUtil.getCellVal(col1) + (row1 + 1) + "*" + CellUtil.getCellVal(col2) + (row2 + 1));
    }

    /**
     * 设置单元格相除公式
     *
     * @param cell cell
     * @param row1 cell1行
     * @param col1 cell1列
     * @param row2 cell2行
     * @param col2 cell2列
     */
    public static void setDivideFormula(Cell cell, int row1, int col1, int row2, int col2) {
        if (cell == null)
            return;
        cell.setCellFormula(CellUtil.getCellVal(col1) + (row1 + 1) + "/" + CellUtil.getCellVal(col2) + (row2 + 1));
    }

    /**
     * 设置单元格求和公式
     *
     * @param cell cell
     * @param row1 cell1行
     * @param col1 cell1列
     * @param row2 cell2行
     * @param col2 cell2列
     */
    public static void setSumFormula(Cell cell, int row1, int col1, int row2, int col2) {
        if (cell == null)
            return;
        cell.setCellFormula(
                "SUM(" + CellUtil.getCellVal(col1) + (row1 + 1) + ":" + CellUtil.getCellVal(col2) + (row2 + 1) + ")");
    }

    /**
     * 设置成功率公式
     *
     * @param cell cell
     * @param row1 cell1行
     * @param col1 cell1列
     * @param row2 cell2行
     * @param col2 cell2列
     */
    public static void setSuccessRateFormula(Cell cell, int row1, int col1, int row2, int col2) {
        if (cell == null)
            return;
        String cell1 = CellUtil.getCellVal(col1) + (row1 + 1);
        String cell2 = CellUtil.getCellVal(col2) + (row2 + 1);
        cell.setCellFormula("(" + cell1 + "-" + cell2 + ")/" + cell1);
    }


//    @Test
//    public void test1() {
//        System.out.println(getCellVal(999));//ALL
//    }
}
