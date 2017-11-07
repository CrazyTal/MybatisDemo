package tk.mybatis.springboot.service;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class RWGInspectionService {
    private Logger logger = LoggerFactory.getLogger(RWGInspectionService.class);

    @Autowired
    InspectionCommonService inspectionCommon;

    public void exportRWGDaily(InspectionDaily inspectionDaily, Workbook workbook, LocalDate date)
            throws ExcelFormatException {

        setTotalBusinessSheet(inspectionDaily, workbook);
        setProvincesSheet(inspectionDaily, workbook);
        setEachProductAndChannelSheet(inspectionDaily, workbook);
        setTransactionAmountSheet(inspectionDaily, workbook);
        setDailyContent(inspectionDaily, workbook, date);
    }

    /**
     * 总业务量
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setTotalBusinessSheet(InspectionDaily inspectionDaily, Workbook workbook)
            throws ExcelFormatException {
        logger.info("Set BusinessVolume Sheet");
        List<BusinessVolume> datas = inspectionDaily.getTotalBusinessVolume();
        String sheetName = PropertiesUtil.getStrVal("sheet_c1");
        Sheet sheet = workbook.getSheet(sheetName);
        Position start = new Position(0, 0);
        Row row = sheet.getRow(start.getRow());
        int safeCounts = 0;

        while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal("sheet_c1_sign").equals(row.getCell(0).toString())) {
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
        //任我购巡检日报模板总量有31行
        CellUtil.clearRows(sheet, start.getRow() + datas.size(), 32 - datas.size());
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
        String sheetName = PropertiesUtil.getStrVal("sheet_c2");
        Sheet sheet = workbook.getSheet(sheetName);
        Position start = new Position(0, 0);
        Row row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal("sheet_c2_sign").equals(row.getCell(0).toString())) {
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
            if (row == null || row.getCell(0) == null || "".equals(row.getCell(0).toString()) || "合计".equals(row.getCell(0).toString()) || "总计".equals(row.getCell(0).toString())) {
                break;
            }
            countRow++;
        }
        CellUtil.clearRows(sheet, start.getRow() + datas.size(), countRow - datas.size());
        //重置excel公式，否则打开时数据不变
        inspectionCommon.setProvincesVal(datas, workbook, sheet, start, countRow);
    }

    /**
     * 分产品：
     * 分渠道：
     * （安全计数做了两次，视为不确定其先后顺序）
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setEachProductAndChannelSheet(InspectionDaily inspectionDaily, Workbook workbook) throws ExcelFormatException {
        logger.info("Set Each Product And Channel sSheet");
        String sheetName = PropertiesUtil.getStrVal("sheet_c3");
        Sheet sheet = workbook.getSheet(sheetName);
        //分产品
        List<EachProductBusiness> productBusinessesDatas = inspectionDaily.getEachProductBusinessVolume();
        Position start = new Position(0, 0);
        Row row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal("sheet_c3_1_sign").equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("EachProductBusinessSheet : not found table in the template excel");
                throw new ExcelFormatException("EachProductBusinessSheet : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("EachProductBusinessSheet start at row : " + start.getRow());
        if (productBusinessesDatas == null || productBusinessesDatas.size() == 0) {
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
        inspectionCommon.setProductsBusinessVal(productBusinessesDatas, sheet, start, countRow);
        //直接删除多余行数
        if (countRow > productBusinessesDatas.size()) {
            CellUtil.removeRows(sheet, start.getRow(), countRow - productBusinessesDatas.size());
        }

        //分渠道
        List<EachChannelBusiness> channelBusinessesDatas = inspectionDaily.getEachChannelBusinessVolume();
        start = new Position(0, 0);
        row = sheet.getRow(start.getRow());
        safeCounts = 0;
        while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal("sheet_c3_2_sign").equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("EachChannelBusinessSheet : not found table in the template excel");
                throw new ExcelFormatException("EachChannelBusinessSheet : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("EachChannelBusinessSheet start at row : " + start.getRow());
        if (channelBusinessesDatas == null || channelBusinessesDatas.size() == 0) {
            CellUtil.clearRows(sheet, start.getRow(), -1);
            return;
        }
        //判断结束行，与数据量进行对比
        countRow = 0;
        while (countRow < InspectionConstants.SAFE_LINE) {
            row = sheet.getRow(start.getRow() + countRow);
            if (row == null || row.getCell(0) == null || "".equals(row.getCell(0).toString()) || "合计".equals(row.getCell(0).toString()) || "总计".equals(row.getCell(0).toString())) {
                break;
            }
            countRow++;
        }
        inspectionCommon.setChannelsBusinessVal(channelBusinessesDatas, sheet, start, countRow);
        //直接删除多余行数
        if (countRow > channelBusinessesDatas.size()) {
            CellUtil.removeRows(sheet, start.getRow(), countRow - channelBusinessesDatas.size());
        }
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
        String sheetName = PropertiesUtil.getStrVal("sheet_c4");
        Sheet sheet = workbook.getSheet(sheetName);
        Position start = new Position(0, 0);
        Row row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal("sheet_c4_sign").equals(row.getCell(0).toString())) {
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
        String sheetName = PropertiesUtil.getStrVal("sheet_c5");
        Sheet sheet = workbook.getSheet(sheetName);
        if (inspectionDaily.getTotalBusinessVolume() == null || inspectionDaily.getTransactionAmount() == null ||
                inspectionDaily.getEachProductBusinessVolume() == null || inspectionDaily.getEachChannelBusinessVolume() == null ||
                inspectionDaily.getProvincesBusinesses() == null) {
            sheet.createRow(0).createCell(0).setCellValue("数据不完整，无法写日报");
            CellUtil.clearRows(sheet, 1, -1);
            return;
        }
        List<CellStyle> cellStyleList = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
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
        String r0c0Str = "总部能力开放平台卓望流量充值巡检日报_" + DateTimeFormatter.ofPattern("yyyyMMdd").format(date);
        dailyContents.add(r0c0Str);
        String r1c0Str = "一、“任我购”业务量情况：";
        dailyContents.add(r1c0Str);
        StringBuffer r2c0Str = new StringBuffer("");
        r2c0Str.append(DateTimeFormatter.ofPattern("MM月dd日").format(date))
                .append("，任我购系列产品交易总量").append(totalBusinessVol).append("笔，业务量环比")
                .append(ringRatio >= 0 ? "上升" : "下降").append(ringRatioStr).append("，涉及交易金额")
                .append(transactionAmountStr).append("万元，交易成功率").append(transactionSuccessRateStr)
                .append("，系统成功率").append(systemSuccessRateStr).append("；其中，");
        dailyContents.add(r2c0Str.toString());
        StringBuffer r3c0Str = new StringBuffer("1、业务类型：");
        List<EachProductBusiness> productList = inspectionDaily.getEachProductBusinessVolume();
        int maxProductsNum = productList.size() < 3 ? productList.size() : 3;
        for (int i = 0; i < maxProductsNum; i++) {
            r3c0Str.append(productList.get(i).getBusiness()).append(productList.get(i).getBusinessSuccess())
                    .append("笔（占比").append(percentFormat.format(productList.get(i).getRatio()));
            if (i < maxProductsNum - 1) {
                r3c0Str.append("）、");
            }
        }
        r3c0Str.append("）；");
        dailyContents.add(r3c0Str.toString());
        StringBuffer r4c0Str = new StringBuffer("");
        List<EachChannelBusiness> channelList = inspectionDaily.getEachChannelBusinessVolume();
        r4c0Str.append("2、交易量排名前三的渠道：");
        int maxChannelsNum = channelList.size() < 3 ? channelList.size() : 3;
        for (int i = 0; i < maxChannelsNum; i++) {
            r4c0Str.append(channelList.get(i).getChannel()).append(channelList.get(i).getBusinessSuccess())
                    .append("笔（占比").append(percentFormat.format(channelList.get(i).getRatio()));
            if (i < maxChannelsNum - 1) {
                r4c0Str.append("）、");
            }
        }
        r4c0Str.append("）；");
        dailyContents.add(r4c0Str.toString());
        StringBuffer r5c0Str = new StringBuffer("");
        List<ProvincesBusiness> provinceList = inspectionDaily.getProvincesBusinesses();
        r5c0Str.append("3、业务量排名前三的省份：");
        int maxBusinessNum = provinceList.size() < 3 ? provinceList.size() : 3;
        for (int i = 0; i < maxBusinessNum; i++) {
            r5c0Str.append(provinceList.get(i).getProvince()).append(provinceList.get(i).getTotalBusinessVolume())
                    .append("笔（").append(percentFormat.format(provinceList.get(i).getRatio()));
            if (i < maxBusinessNum - 1) {
                r5c0Str.append("）、");
            }
        }
        r5c0Str.append("）；");
        dailyContents.add(r5c0Str.toString());
        List<ProvincesBusiness> sortedProvinces = new ArrayList<>();
        sortedProvinces.addAll(inspectionDaily.getProvincesBusinesses());
        sortedProvinces.sort((o1, o2) -> {
            return o1.getTransactionSuccessRate() >= o2.getTransactionSuccessRate() ? 1 : -1;
        });
        StringBuffer r6c0Str = new StringBuffer("");
        r6c0Str.append("4、成功率排名后三的省份：");
        int miniSuccessNum = sortedProvinces.size() < 3 ? sortedProvinces.size() : 3;
        for (int i = 0; i < miniSuccessNum; i++) {
            r6c0Str.append(sortedProvinces.get(i).getProvince()).append("（")
                    .append(percentFormat.format(sortedProvinces.get(i).getTransactionSuccessRate()));
            if (i < miniSuccessNum - 1) {
                r6c0Str.append("）、");
            }
        }
        r6c0Str.append("）；");
        dailyContents.add(r6c0Str.toString());
        String r7c0Str = "3、问题原因：";
        dailyContents.add(r7c0Str);
        int contentSize = 8;
        for (int i = 0; i < contentSize; i++) {
            if (i == 0) {
                sheet.createRow(i).createCell(0).setCellStyle(cellStyleList.get(0));
            } else if (i == 1) {
                sheet.createRow(i).createCell(0).setCellStyle(cellStyleList.get(1));
            } else {
                sheet.createRow(i).createCell(0).setCellStyle(cellStyleList.get(2));
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
