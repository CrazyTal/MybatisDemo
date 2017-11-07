package tk.mybatis.springboot.service;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.springboot.exception.ExcelFormatException;
import tk.mybatis.springboot.model.*;
import tk.mybatis.springboot.util.CellUtil;
import tk.mybatis.springboot.util.InspectionConstants;
import tk.mybatis.springboot.util.PropertiesUtil;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RWKInspectionService {
    private Logger logger = LoggerFactory.getLogger(RWKInspectionService.class);

    @Autowired
    InspectionCommonService inspectionCommon;

    public void exportRWKDaily(InspectionDaily inspectionDaily, Workbook workbook, LocalDate date)
            throws ExcelFormatException {

        setTotalBusinessSheet(inspectionDaily, workbook);
        setEachAPPSale(inspectionDaily, workbook, date);
        setProvincesSheet(inspectionDaily, workbook);
        setProvinceDoDSheet(inspectionDaily, workbook, date);
        setTransactionAmountSheet(inspectionDaily, workbook);
        setDailyContent(inspectionDaily, workbook, date);
    }

    /**
     * 任我看总体情况
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setTotalBusinessSheet(InspectionDaily inspectionDaily, Workbook workbook) throws ExcelFormatException {
        logger.info("Set BusinessVolume Sheet");
        List<BusinessVolume> datas = inspectionDaily.getTotalBusinessVolume();
        String sheetName = PropertiesUtil.getStrVal("sheet_d1");
        Sheet sheet = workbook.getSheet(sheetName);
        Position start = new Position(0, 0);
        Row row = sheet.getRow(start.getRow());
        int safeCounts = 0;

        while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal("sheet_d1_sign").equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("BusinessVolumeSheet : not found table in the template excel");
                throw new ExcelFormatException("BusinessVolumeSheet : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("BusinessVolumeSheet start at row : " + start.getRow());

        if (datas == null || datas.size() == 0) {
            CellUtil.clearRows(sheet, start.getRow(), -1);
            return;
        }
        inspectionCommon.setTotalBusinessVal(datas, sheet, start);
        //任我看巡检日报模板总量有32行
        CellUtil.clearRows(sheet, start.getRow() + datas.size(), 33 - datas.size());
    }

    /**
     * 各APP销售情况：
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setEachAPPSale(InspectionDaily inspectionDaily, Workbook workbook, LocalDate date)
            throws ExcelFormatException {
        logger.info("Set EachAPPSale Sheet");
        Map<String, EachAPPSale> eachAPPSale = new LinkedHashMap<>(inspectionDaily.getEachAPPSale().size() - 1);
        inspectionDaily.getEachAPPSale().entrySet().stream()
                .filter(
                        appMap -> !InspectionConstants.TOTAL.equals(appMap.getKey())
                ).forEach(
                appMap -> {
                    eachAPPSale.put(appMap.getKey(), appMap.getValue());
                });
        Set<String> productSet = inspectionDaily.getEachAPPSale().get(InspectionConstants.TOTAL).getProducts().keySet();
        String sheetName = PropertiesUtil.getStrVal("sheet_d2");
        Sheet sheet = workbook.getSheet(sheetName);
        Position start = new Position(0, 0);
        Row row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal("sheet_d2_sign").equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("EachAPPSale : not found table in the template excel");
                throw new ExcelFormatException("EachAPPSale : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        //"价格分类单元格占两行"
        start.setRow(start.getRow() + safeCounts + 1);
//        start.setCol(start.getCol());
        logger.info("EachAPPSaleSheet start at row : " + start.getRow());
        if (eachAPPSale == null || eachAPPSale.size() == 0) {
            CellUtil.clearRows(sheet, start.getRow() + 1, -1);
            return;
        }
        sheet.getRow(start.getRow() - 1).getCell(start.getCol() + 1).setCellValue(
                DateTimeFormatter.ofPattern("yyyyMMdd").format(date) + "任我看各项目销量图");
        for (int i = 0; i < productSet.size() + 1; i++) {
            row = sheet.getRow(start.getRow() + 1 + i);
            CellUtil.setSumFormula(row.getCell(start.getCol() + eachAPPSale.size() + 1), row.getRowNum(),
                    start.getCol() + 1, row.getRowNum(), start.getCol() + eachAPPSale.size());
        }
        Cell Cell;
        int appIndex = 1;
        for (Map.Entry<String, EachAPPSale> app : eachAPPSale.entrySet()) {
            row = sheet.getRow(start.getRow());
            Cell = row.getCell(start.getCol() + appIndex);
            Cell.setCellValue(app.getKey());
            for (int i = 0; i < productSet.size() + 2; i++) {
                row = sheet.getRow(start.getRow() + i + 1);
                if (i < productSet.size()) {
                    if (app.getValue().getProducts().get(row.getCell(0).toString()) == null) {
                        row.getCell(start.getCol() + appIndex).setCellValue(0);
                    } else {
                        row.getCell(start.getCol() + appIndex).setCellValue(app.getValue().getProducts().get(row.getCell(0).toString()));
                    }
                } else {
                    if (i == productSet.size()) {
                        //合计行设置公式
                        CellUtil.setSumFormula(row.getCell(start.getCol() + appIndex), start.getRow() + 1,
                                start.getCol() + appIndex, row.getRowNum() - 1, start.getCol() + appIndex);
                    } else {
                        //占比行设置公式
                        CellUtil.setDivideFormula(row.getCell(start.getCol() + appIndex), row.getRowNum() - 1,
                                start.getCol() + appIndex, row.getRowNum() - 1, start.getCol() + eachAPPSale.size() + 1);
                        //占比行最后一个
                        if (appIndex == eachAPPSale.size()) {
                            CellUtil.setDivideFormula(row.getCell(start.getCol() + appIndex + 1), row.getRowNum() - 1,
                                    start.getCol() + appIndex + 1, row.getRowNum() - 1, start.getCol() + appIndex + 1);
                        }
                    }

                }
            }
            appIndex++;
        }


    }

    /**
     * 分省：
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setProvincesSheet(InspectionDaily inspectionDaily, Workbook workbook) throws ExcelFormatException {
        logger.info("Set Provinces Sheet");
        List<ProvincesBusiness> datas = inspectionDaily.getProvincesBusinesses();
        String sheetName = PropertiesUtil.getStrVal("sheet_d3");
        Sheet sheet = workbook.getSheet(sheetName);
        Position start = new Position(0, 0);
        Row row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal("sheet_d3_sign").equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("ProvincesSheet : not found table in the template excel");
                throw new ExcelFormatException("ProvincesSheet : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("ProvincesBusinessSheet start at row : " + start.getRow());
        if (datas == null || datas.size() == 0) {
            CellUtil.clearRows(sheet, start.getRow(), -1);
            return;
        }
        //判断结束行，与数据量进行对比
        int countRow = 0;
        while (countRow < InspectionConstants.SAFE_LINE) {
            row = sheet.getRow(start.getRow() + countRow);
            if (row == null || row.getCell(0) == null || "".equals(row.getCell(0).toString()) ||
                    "合计".equals(row.getCell(0).toString()) || "总计".equals(row.getCell(0).toString())) {
                break;
            }
            countRow++;
        }
        CellUtil.clearRows(sheet, start.getRow() + datas.size(), countRow - datas.size());
        inspectionCommon.setProvincesVal(datas, workbook, sheet, start, countRow);
    }

    /**
     * 分省环比：
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setProvinceDoDSheet(InspectionDaily inspectionDaily, Workbook workbook, LocalDate date)
            throws ExcelFormatException {
        logger.info("Set Province DoD Sheet");
        List<ProvincesDoD> datas = inspectionDaily.getProvincesDoD();
        String sheetName = PropertiesUtil.getStrVal("sheet_d4");
        Sheet sheet = workbook.getSheet(sheetName);
        Position start = new Position(0, 0);
        Row row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal("sheet_d4_sign").equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("ProvinceDoDSheet : not found table in the template excel");
                throw new ExcelFormatException("ProvinceDoDSheet : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("ProvincesDoDSheet start at row : " + start.getRow());
        inspectionCommon.setProvinceDoDHeader(date, sheet, start);
        if (datas == null || datas.size() == 0) {
            CellUtil.clearRows(sheet, start.getRow(), -1);
            return;
        }
        //判断结束行，与数据量进行对比
        int countRow = 0;
        while (countRow < InspectionConstants.SAFE_LINE) {
            row = sheet.getRow(start.getRow() + countRow);
            if (row == null || row.getCell(0) == null || "".equals(row.getCell(0).toString()) || "合计".equals(row.getCell(0).toString()) || "总计".equals(row.getCell(0).toString())) {
                break;
            }
            countRow++;
        }
        CellUtil.clearRows(sheet, start.getRow() + datas.size(), countRow - datas.size());
        inspectionCommon.setProvinceDoDVal(datas, sheet, start);
    }

    /**
     * 交易金额：
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setTransactionAmountSheet(InspectionDaily inspectionDaily, Workbook workbook) throws ExcelFormatException {
        logger.info("Set Transaction Amount Sheet");
        List<TransactionAmount> datas = inspectionDaily.getTransactionAmount();
        String sheetName = PropertiesUtil.getStrVal("sheet_d5");
        Sheet sheet = workbook.getSheet(sheetName);
        Position start = new Position(0, 0);
        Row row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal("sheet_d5_sign").equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("TransactionAmountSheet : not found table in the template excel");
                throw new ExcelFormatException("TransactionAmountSheet : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("TransactionAmountSheet start at row : " + start.getRow());
        if (datas == null || datas.size() == 0) {
            CellUtil.clearRows(sheet, start.getRow(), -1);
            return;
        }
        inspectionCommon.setTransactionVal(datas, workbook, sheet, start, InspectionConstants.SUBTOTAL);
    }


    /**
     * 日报内容：
     *
     * @param inspectionDaily
     * @param workbook
     * @param date
     */
    private void setDailyContent(InspectionDaily inspectionDaily, Workbook workbook, LocalDate date)
            throws ExcelFormatException {
        logger.info("Set Daily Content Sheet");
        String sheetName = PropertiesUtil.getStrVal("sheet_d6");
        Sheet sheet = workbook.getSheet(sheetName);
        if (inspectionDaily.getTotalBusinessVolume() == null || inspectionDaily.getTransactionAmount() == null ||
                inspectionDaily.getEachAPPSale() == null || inspectionDaily.getProvincesBusinesses() == null) {
            sheet.createRow(0).createCell(0).setCellValue("数据不完整，无法写日报");
            CellUtil.clearRows(sheet, 1, -1);
            return;
        }
        List<CellStyle> cellStyleList = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            if (sheet.getRow(i) != null && sheet.getRow(i).getCell(0) != null) {
                cellStyleList.add(sheet.getRow(i).getCell(0).getCellStyle());
            }
        }
        DecimalFormat percentFormat = new DecimalFormat(InspectionConstants.PERCENT_FORMAT);
        BusinessVolume lastBusinessVolume = inspectionDaily.getTotalBusinessVolume().get(inspectionDaily.getTotalBusinessVolume().size() - 1);
        int totalBusinessVol = lastBusinessVolume.getTotalBusinessVolume();
        double ringRatio = lastBusinessVolume.getRingRate();
        String ringRatioStr = percentFormat.format(Math.abs(ringRatio));
        double transactionAmount = 0;
        for (TransactionAmount amount : inspectionDaily.getTransactionAmount()) {
            transactionAmount += amount.getTransactionAmount();
        }
        String transactionAmountStr = new DecimalFormat("0.00").format((double) (transactionAmount / 100) / 100);
        String transactionSuccessRateStr = percentFormat.format(lastBusinessVolume.getTransactionSuccessRate());
        String systemSuccessRateStr = percentFormat.format(lastBusinessVolume.getSystemSuccessRate());

        List<String> dailyContents = new ArrayList<>(10);
        String r0c0Str = "二、“任我看”业务量情况：";
        dailyContents.add(r0c0Str);
        StringBuffer r1c0Str = new StringBuffer("");
        r1c0Str.append(DateTimeFormatter.ofPattern("MM月dd日").format(date))
                .append("，任我看产品交易总量").append(totalBusinessVol).append("笔，业务量环比")
                .append(ringRatio >= 0 ? "上升" : "下降").append(ringRatioStr).append("，涉及交易金额")
                .append(transactionAmountStr).append("万元，交易成功率").append(transactionSuccessRateStr)
                .append("，系统成功率").append(systemSuccessRateStr).append("；其中，");
        dailyContents.add(r1c0Str.toString());
        StringBuffer r2c0Str = new StringBuffer("1、交易业务量排名前三的APP：");
        Map<String, EachAPPSale> appSaleMap = inspectionDaily.getEachAPPSale();
        Iterator<String> apps = appSaleMap.keySet().iterator();
        int index = 0;
        String app;
        while (apps.hasNext()) {
            app = apps.next();
            r2c0Str.append(app).append(appSaleMap.get(app).getTotal()).append("笔（占比").append(percentFormat.format(appSaleMap.get(app).getRatio()));
            if (index >= 2) {
                r2c0Str.append("）；");
                break;
            }
            r2c0Str.append("）、");
            index++;
        }
        dailyContents.add(r2c0Str.toString());
        StringBuffer r3c0Str = new StringBuffer("2、业务量排名前三的省份：");
        List<ProvincesBusiness> provinceList = inspectionDaily.getProvincesBusinesses();
        int maxBusinessNum = provinceList.size() < 3 ? provinceList.size() : 3;
        for (int i = 0; i < maxBusinessNum; i++) {
            r3c0Str.append(provinceList.get(i).getProvince()).append(provinceList.get(i).getTotalBusinessVolume())
                    .append("笔（").append(percentFormat.format(provinceList.get(i).getRatio()));
            if (i < maxBusinessNum - 1) {
                r3c0Str.append("）、");
            }
        }
        r3c0Str.append("）；");
        dailyContents.add(r3c0Str.toString());
        List<ProvincesBusiness> sortedProvinces = new ArrayList<>();
        sortedProvinces.addAll(inspectionDaily.getProvincesBusinesses());
        sortedProvinces.sort((o1, o2) -> {
            return o1.getTransactionSuccessRate() >= o2.getTransactionSuccessRate() ? 1 : -1;
        });
        StringBuffer r4c0Str = new StringBuffer("3、成功率排名后三的省份：");
        int miniSuccessNum = sortedProvinces.size() < 3 ? sortedProvinces.size() : 3;
        for (int i = 0; i < miniSuccessNum; i++) {
            r4c0Str.append(sortedProvinces.get(i).getProvince()).append("（")
                    .append(percentFormat.format(sortedProvinces.get(i).getTransactionSuccessRate()));
            if (i < miniSuccessNum - 1) {
                r4c0Str.append("）、");
            }
        }
        r4c0Str.append("）；");
        dailyContents.add(r4c0Str.toString());
        String r5c0Str = "3、问题原因：";
        dailyContents.add(r5c0Str);
        int contentSize = 6;
        for (int i = 0; i < contentSize; i++) {
            if (i == 0) {
                sheet.createRow(i).createCell(0).setCellStyle(cellStyleList.get(0));
            } else {
                sheet.createRow(i).createCell(0).setCellStyle(cellStyleList.get(1));
            }
            sheet.getRow(i).getCell(0).setCellValue(dailyContents.get(i));
        }
        for (int i = 0; i < 3; i++) {
            if (sheet.getRow(contentSize + i) != null && sheet.getRow(contentSize + i).getCell(0) != null) {
                sheet.getRow(contentSize + i).getCell(0).setCellValue("");
            }
        }
    }
}
