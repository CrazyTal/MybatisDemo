package tk.mybatis.springboot.util;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class FileUtil {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取文件类型
     *
     * @param in       文件流
     * @param filename 文件名
     * @return 工作簿
     * @throws IOException
     */
    public static Workbook getWorkbook(InputStream in, String filename) throws IOException {
        if (!in.markSupported()) {
            in = new PushbackInputStream(in, 8);
        }
        Workbook wb = null;
        if (POIFSFileSystem.hasPOIFSHeader(in) || filename.endsWith(".xls")) {
            wb = new HSSFWorkbook(in);// Excel 2003
        } else if (POIXMLDocument.hasOOXMLHeader(in) || filename.endsWith("xlsx")) {
            wb = new XSSFWorkbook(in);// Excel 2007
        }
        return wb;
    }

    /**
     * 获取单元格类型，返回转换后的值
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        if (cell == null)
            return "";
        String cellValue = null;
        int cellType = cell.getCellType();
        switch (cellType) {
            case Cell.CELL_TYPE_STRING: // 文本
                cellValue = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = fmt.format(cell.getDateCellValue()); // 日期型
                } else {
                    cellValue = df.format(cell.getNumericCellValue());// 数字型
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN: // 布尔型
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_BLANK: // 空白
                cellValue = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_ERROR: // 错误
                cellValue = "读取错误";
                break;
            case Cell.CELL_TYPE_FORMULA: // 公式
                try {
                    cellValue = df.format(cell.getNumericCellValue());
                    // cellValue = new DecimalFormat("#").format(Value);// 数字型
                } catch (IllegalStateException e) {
                    // cellValue = String.valueOf(cell.getRichStringCellValue());
                    cellValue = " ";
                }
                break;
            default:
                cellValue = "无法读取";
        }
        if (cellValue.contains(".00")) {
            cellValue = cellValue.substring(0, cellValue.indexOf(".00"));
        }
        if (cellValue.contains("#")) {
            cellValue = "0";
        }
        return cellValue;
    }

    /**
     * 按文件路径删除文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                while (files.length != 0) {
                    for (File f : files) {
                        f.delete();
                        files = file.listFiles();
                    }
                }
            }
            file.delete();
            return true;
        } else {
            return false;
        }

    }
}
