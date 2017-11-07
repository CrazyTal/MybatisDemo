package tk.mybatis.springboot.service;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import tk.mybatis.springboot.model.*;
import tk.mybatis.springboot.util.CellUtil;
import tk.mybatis.springboot.util.InspectionConstants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务巡检通用逻辑代码
 * 避免冗余代码
 */
@Service
public class InspectionCommonService {

    /**
     * 写入分产品数据
     *
     * @param productBusinessesDatas 数据
     * @param sheet                  sheet
     * @param start                  起始位置
     * @param countRow               表格行数
     */
    public void setProductsBusinessVal(List<EachProductBusiness> productBusinessesDatas, Sheet sheet, Position start, int countRow) {
        Row row;
        int countTotalRow = 0;
        //重置excel公式，否则打开时数据不变
        while (countTotalRow < InspectionConstants.SAFE_LINE) {
            row = sheet.getRow(start.getRow() + countTotalRow);
            if (row != null && row.getCell(0) != null && ("合计".equals(row.getCell(0).toString())
                    || "总计".equals(row.getCell(0).toString()))) {
                //不管是否有数据，都会加上模板存在的对应行数
                CellUtil.setSumFormula(row.getCell(1), start.getRow(), 1, start.getRow() - 1 + countRow, 1);
                CellUtil.setSumFormula(row.getCell(2), start.getRow(), 2, start.getRow() - 1 + countRow, 2);
                break;
            }
            countTotalRow++;
        }
        for (int i = 0; i < productBusinessesDatas.size(); i++) {
            row = sheet.getRow(start.getRow() + i);
            row.getCell(0).setCellValue(productBusinessesDatas.get(i).getBusiness());
            row.getCell(1).setCellValue(productBusinessesDatas.get(i).getBusinessSuccess());
            CellUtil.setDivideFormula(row.getCell(2), row.getRowNum(), 1, start.getRow() + countTotalRow, 1);
        }
    }

    /**
     * 写入分渠道数据
     *
     * @param channelBusinessesDatas 数据
     * @param sheet                  sheet
     * @param start                  起始位置
     * @param countRow               表格行数
     */
    public void setChannelsBusinessVal(List<EachChannelBusiness> channelBusinessesDatas, Sheet sheet, Position start, int countRow) {
        Row row;
        //重置excel公式，否则打开时数据不变
        int countTotalRow = 0;
        while (countTotalRow < InspectionConstants.SAFE_LINE) {
            row = sheet.getRow(start.getRow() + countTotalRow);
            if (row != null && row.getCell(0) != null && ("合计".equals(row.getCell(0).toString())
                    || "总计".equals(row.getCell(0).toString()))) {
                //不管是否有数据，都会加上模板存在的对应行数
                CellUtil.setSumFormula(row.getCell(1), start.getRow(), 1, start.getRow() + countRow - 1, 1);
                CellUtil.setSumFormula(row.getCell(2), start.getRow(), 2, start.getRow() + countRow - 1, 2);
                break;
            }
            countTotalRow++;
        }
        for (int i = 0; i < channelBusinessesDatas.size(); i++) {
            row = sheet.getRow(start.getRow() + i);
            row.getCell(0).setCellValue(channelBusinessesDatas.get(i).getChannel());
            row.getCell(1).setCellValue(channelBusinessesDatas.get(i).getBusinessSuccess());
            CellUtil.setDivideFormula(row.getCell(2), row.getRowNum(), 1, start.getRow() + countTotalRow, 1);
        }
    }


    /**
     * 写入分省数据
     *
     * @param datas    数据
     * @param workbook workbook
     * @param sheet    sheet
     * @param start    起始位置
     * @param countRow 表格行数
     * @return
     */
    public void setProvincesVal(List<ProvincesBusiness> datas, Workbook workbook, Sheet sheet, Position start, int countRow) {
        Row row;
        int countTotalRow = 0;
        //重置excel公式，否则打开时数据不变
        while (countTotalRow < 40) {
            row = sheet.getRow(start.getRow() + countTotalRow);
            if (row != null && row.getCell(0) != null
                    && ("合计".equals(row.getCell(0).toString()) || "总计".equals(row.getCell(0).toString()))) {
                // 不管是否有数据，都会加上模板存在的对应行数
                CellUtil.setSumFormula(row.getCell(1), start.getRow(), 1, start.getRow() + countRow - 1, 1);
                CellUtil.setSumFormula(row.getCell(2), start.getRow(), 2, start.getRow() + countRow - 1, 2);
                CellUtil.setSuccessRateFormula(row.getCell(3), start.getRow() + countTotalRow, 1,
                        start.getRow() + countTotalRow, 2);
                CellUtil.setSumFormula(row.getCell(4), start.getRow(), 4, start.getRow() + countRow - 1, 4);
                break;
            }
            countTotalRow++;
        }
        Font normalFont = workbook.createFont();
        normalFont.setFontName("微软雅黑");
        normalFont.setFontHeightInPoints((short) 8);
        Map<String, Row> rowMap = new HashMap<>();
        for (int i = 0; i < datas.size(); i++) {
            row = sheet.getRow(start.getRow() + i);
            row.getCell(0).setCellValue(datas.get(i).getProvince());
            row.getCell(1).setCellValue(datas.get(i).getTotalBusinessVolume());
            row.getCell(2).setCellValue(datas.get(i).getTransactionFailure());
            CellUtil.setSuccessRateFormula(row.getCell(3), row.getRowNum(), 1, row.getRowNum(), 2);
            CellUtil.setDivideFormula(row.getCell(4), row.getRowNum(), 1, start.getRow() + countTotalRow, 1);
            for (int j = 0; j < 5; j++) {
                row.getCell(j).getCellStyle().setFont(normalFont);
            }
            rowMap.put(datas.get(i).getProvince(), row);
        }
        List<ProvincesBusiness> sortedData = new ArrayList<>();
        sortedData.addAll(datas);
        sortedData.sort((o1, o2) -> {
            return o1.getTransactionSuccessRate() >= o2.getTransactionSuccessRate() ? 1 : -1;
        });
        CellStyle percentCellStyle = getDefaultCellStyle(workbook);
        percentCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(InspectionConstants.PERCENT_FORMAT));
        CellStyle cellStyle = getDefaultCellStyle(workbook);
        Font redFont = workbook.createFont();
        redFont.setFontName("微软雅黑");
        redFont.setFontHeightInPoints((short) 8);
        redFont.setColor((short) 2);
        cellStyle.setFont(redFont);
        percentCellStyle.setFont(redFont);
        //防止省份数小于3时报错
        int miniProvincesNum = rowMap.size() < 3 ? rowMap.size() : 3;
        for (int i = 0; i < miniProvincesNum; i++) {
            for (int j = 0; j < 5; j++) {
                if (j >= 3) {
                    rowMap.get(sortedData.get(i).getProvince()).getCell(j).setCellStyle(percentCellStyle);
                } else {
                    rowMap.get(sortedData.get(i).getProvince()).getCell(j).setCellStyle(cellStyle);
                }
            }
        }
    }

    /**
     * 写入总业务量数据
     *
     * @param datas 数据
     * @param sheet sheet
     * @param start 起始位置
     */
    public void setTotalBusinessVal(List<BusinessVolume> datas, Sheet sheet, Position start) {
        Row row;
        for (int i = 0; i < datas.size(); i++) {
            row = sheet.getRow(start.getRow() + i);
            row.getCell(0).setCellValue(datas.get(i).getDate());
            row.getCell(1).setCellValue(datas.get(i).getTotalBusinessVolume());
            row.getCell(2).setCellValue(datas.get(i).getTransactionFailure());
            CellUtil.setSuccessRateFormula(row.getCell(3), row.getRowNum(), 1, row.getRowNum(), 2);
            row.getCell(4).setCellValue(datas.get(i).getSystemFailure());
            CellUtil.setSuccessRateFormula(row.getCell(5), row.getRowNum(), 1, row.getRowNum(), 4);
            row.getCell(6).setCellFormula("(" + CellUtil.getCellVal(1) + (row.getRowNum() + 1) + "-" +
                    CellUtil.getCellVal(1) + row.getRowNum() + ")/" + CellUtil.getCellVal(1) + row.getRowNum());
        }
    }

    /**
     * 写入分省环比表头日期
     *
     * @param date  日期
     * @param sheet sheet
     * @param start 起始位置
     */
    public void setProvinceDoDHeader(LocalDate date, Sheet sheet, Position start) {
        Row row;
        row = sheet.getRow(start.getRow() - 1);
        row.getCell(1).setCellValue(DateTimeFormatter.ofPattern("yyyyMMdd").format(date.plusDays(-1)));
        row.getCell(2).setCellValue(DateTimeFormatter.ofPattern("yyyyMMdd").format(date));
    }

    /**
     * 写入分省环比数据
     *
     * @param datas 数据
     * @param sheet sheet
     * @param start 起始位置
     */
    public void setProvinceDoDVal(List<ProvincesDoD> datas, Sheet sheet, Position start) {
        Row row;
        for (int i = 0; i < datas.size(); i++) {
            row = sheet.getRow(start.getRow() + i);
            row.getCell(0).setCellValue(datas.get(i).getProvince());
            row.getCell(1).setCellValue(datas.get(i).getBusinessVolumeT_1());
            row.getCell(2).setCellValue(datas.get(i).getBusinessVolumeT());
        }
    }

    /**
     * 写入交易金额数据
     *
     * @param datas     数据
     * @param workbook  workbook
     * @param sheet     sheet
     * @param start     起始位置
     * @param totalName 合计名
     */
    public void setTransactionVal(List<TransactionAmount> datas, Workbook workbook, Sheet sheet, Position start, String totalName) {
        Row row;
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.cloneStyleFrom(sheet.getRow(start.getRow()).getCell(0).getCellStyle());
        //清除行
        CellUtil.clearRows(sheet, start.getRow(), -1);
        for (int i = 0; i < datas.size(); i++) {
            row = sheet.createRow(start.getRow() + i);
            row.createCell(0).setCellValue(datas.get(i).getProduct());
            row.createCell(1).setCellValue(datas.get(i).getPrice());
            row.createCell(2).setCellValue(datas.get(i).getSingleDayAmount());
            CellUtil.setMultiplyFormula(row.createCell(3), row.getRowNum(), 1, row.getRowNum(), 2);
            for (int j = 0; j < 4; j++) {
                row.getCell(j).setCellStyle(cellStyle);
            }
        }
        CellStyle cellStyleAno = getDefaultCellStyle(workbook);
        cellStyleAno.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyleAno.setFillForegroundColor((short) 47);
//        byte[] bytes = {-3, -23, -39};
//        cellStyleAno.setFillForegroundColor(new Color(bytes));
        row = sheet.createRow(start.getRow() + datas.size());
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                row.createCell(i).setCellValue(totalName);
            } else {
                row.createCell(i).setCellFormula("SUM(" + CellUtil.getCellVal(i) + (start.getRow() + 1) + ":" + CellUtil.getCellVal(i) + (row.getRowNum()) + ")");
            }
            row.getCell(i).setCellStyle(cellStyleAno);
        }
    }

    /**
     * 获取单元格默认格式
     *
     * @param workbook
     * @return
     */
    private CellStyle getDefaultCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        Font font = workbook.createFont();
        font.setFontName("微软雅黑");
        font.setFontHeightInPoints((short) 8);
        cellStyle.setWrapText(true);
        cellStyle.setFont(font);
        return cellStyle;
    }
}
