package tk.mybatis.springboot.service;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tk.mybatis.springboot.exception.ExcelFormatException;
import tk.mybatis.springboot.model.*;
import tk.mybatis.springboot.util.APPSaleMapComparator;
import tk.mybatis.springboot.util.CellUtil;
import tk.mybatis.springboot.util.GenerateDataUtil;
import tk.mybatis.springboot.util.InspectionConstants;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by ltao on 2017/7/26.
 */
@Service
public class ExportExcelService {
    private Logger logger = LoggerFactory.getLogger(ExportExcelService.class);

    public void exportExcel(HttpServletResponse response, Date date, String type, Map<String, Object> resultMap) throws IOException {
        OutputStream os = null;
        // TODO: 2017/8/9 findDataByDate
        // TODO: 2017/8/11 日报内容
        /**
         * 当写入ecel值时在最底层留下空格，用代码判断行数是否和数据库读取的数据相同，若是不同则提示用户
         * 交易金额sheet：如果采用全部由数据库读写，则需将excel模板中数据清除，或只留下一条示例记录
         * 因为excel中存在公式如果覆盖掉，打开excel后会显示文件错误，没什么影响，就是每次都会有提示。
         */
        InspectionDaily inspectionDaily = GenerateDataUtil.getDatas();
        // TODO: 2017/8/22 此处拿到的数据可分为两种，1：缓存中读取到的规则的数据，2：数据库中需要计算的数据
        try {

            serResponse(response);
            os = response.getOutputStream();
            generateExcel(inspectionDaily, os, type, resultMap);

        } catch (IOException e) {
            logger.error("Export Excel IOException !");
            throw new IOException("Export Excel IOException", e);
        } finally {
            closeOutputStream(os);
        }
    }

    private void generateExcel(InspectionDaily inspectionDaily, OutputStream os, String type, Map<String, Object> resultMap) throws IOException {
        if (inspectionDaily.getTotalBusinessVolume() == null) {
            resultMap.put("result", "failed");
            resultMap.put("message", "no data !");
            logger.warn("No data found !");
            return;
        }
        logger.info("Excel type : " + type);
        if (!InspectionConstants.PATHMAP.containsKey(type)) {
            resultMap.put("result", "failed");
            resultMap.put("message", "no excel template corresponding to type !");
            logger.warn("There is no correct Excel template !");
            return;
        }
        File file = new File(InspectionConstants.PATHMAP.get(type));
        InputStream is = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        try {
            switch (type) {
                case "totalBusinessVolume":
                    setBusinessVolumeSheet(inspectionDaily, workbook);
                    setDataRechargeSheet(inspectionDaily, workbook);
                    setEachProductAndChannelSheet(inspectionDaily, workbook);
                    setProvincesSheet(inspectionDaily, workbook);
                    setProvinceDoDSheet(inspectionDaily, workbook);
                    setTransactionAmountSheet(inspectionDaily, workbook);
//                    setEachAPPSale(inspectionDaily, workbook);//各APP
                    break;
                case "zhuowang":

                    break;
                case "renwogou":

                    break;
                case "renwokan":

                    break;
                default:
                    logger.warn("Type is not correct");
                    break;
            }

        } catch (ExcelFormatException e) {
            resultMap.put("result", "failed");
            resultMap.put("message", e.getMessage());
            return;
        }
        workbook.write(os);
    }

    /**
     * 总业务量
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setBusinessVolumeSheet(InspectionDaily inspectionDaily, XSSFWorkbook workbook) throws ExcelFormatException {
        logger.info("Set BusinessVolume Sheet");
        List<BusinessVolume> datas = inspectionDaily.getTotalBusinessVolume();
        XSSFSheet sheet = workbook.getSheet(InspectionConstants.SHEET_A1);
        Position start = new Position(0, 0);
        XSSFRow row = sheet.getRow(start.getRow());
        int safeCounts = 0;

        while (row.getCell(0) == null || !InspectionConstants.SHEET_A1_SIGN.equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("BusinessVolumeSheet : not found table in the template excel");
                throw new ExcelFormatException("BusinessVolumeSheet : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("BusinessVolumeSheet start at row : " + start.getRow());

        if (datas == null) {
            return;
        }
        for (int i = 0; i < datas.size(); i++) {
            row = sheet.getRow(start.getRow() + i);
            row.getCell(0).setCellValue(datas.get(i).getDate());
            row.getCell(1).setCellValue(datas.get(i).getTotalBusinessVolume());
            row.getCell(2).setCellValue(datas.get(i).getTransactionFailure());
            row.getCell(4).setCellValue(datas.get(i).getSystemFailure());
        }
    }

    /**
     * 流量直充：
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setDataRechargeSheet(InspectionDaily inspectionDaily, XSSFWorkbook workbook) throws ExcelFormatException {
        logger.info("Set DataRecharge Sheet");
        List<DataRecharge> datas = inspectionDaily.getDataRecharge();
        XSSFSheet sheet = workbook.getSheet(InspectionConstants.SHEET_A2);
        Position start = new Position(0, 0);
        XSSFRow row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !InspectionConstants.SHEET_A2_SIGN.equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("DataRechargeSheet : not found table in the template excel");
                throw new ExcelFormatException("DataRechargeSheet : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("DataRechargeSheet start at row : " + start.getRow());
        if (datas == null) {
            return;
        }
        for (int i = 0; i < datas.size(); i++) {
            row = sheet.getRow(start.getRow() + i);
            row.getCell(0).setCellValue(datas.get(i).getDate());
            row.getCell(1).setCellValue(datas.get(i).getTotalBusinessVolume());
            row.getCell(2).setCellValue(datas.get(i).getTransactionFailure());
        }
    }

    /**
     * 分产品：
     * 分渠道：
     * （安全计数做了两次，因为视为不确定其先后顺序）
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setEachProductAndChannelSheet(InspectionDaily inspectionDaily, XSSFWorkbook workbook) throws ExcelFormatException {
        logger.info("Set Each Product And Channel sSheet");
        XSSFSheet sheet = workbook.getSheet(InspectionConstants.SHEET_A3);
        List<EachProductBusiness> productBusinessesDatas = inspectionDaily.getEachProductBusinessVolume();
        Position start = new Position(0, 0);
        XSSFRow row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !InspectionConstants.SHEET_A3_1_SIGN.equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("EachProductBusinessSheet : not found table in the template excel");
                throw new ExcelFormatException("EachProductBusinessSheet : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("EachProductBusinessSheet start at row : " + start.getRow());
        if (productBusinessesDatas == null) {
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
        if (productBusinessesDatas.size() > countRow) {
            logger.warn("EachProductBusinessSheet : The data length exceeds the number of template reservation lines");
            throw new ExcelFormatException("EachProductBusinessSheet : Please add " + (productBusinessesDatas.size() - countRow) + " lines of the form template");
        }
        for (int i = 0; i < productBusinessesDatas.size(); i++) {
            row = sheet.getRow(start.getRow() + i);
            row.getCell(0).setCellValue(productBusinessesDatas.get(i).getBusiness());
            row.getCell(1).setCellValue(productBusinessesDatas.get(i).getBusinessSuccess());
        }

        List<EachChannelBusiness> channelBusinessesDatas = inspectionDaily.getEachChannelBusinessVolume();
        start = new Position(0, 0);
        row = sheet.getRow(start.getRow());
        safeCounts = 0;
        while (row.getCell(0) == null || !InspectionConstants.SHEET_A3_2_SIGN.equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("EachChannelBusinessSheet : not found table in the template excel");
                throw new ExcelFormatException("EachChannelBusinessSheet : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("EachChannelBusinessSheet start at row : " + start.getRow());
        if (channelBusinessesDatas == null) {
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
        if (channelBusinessesDatas.size() > countRow) {
            logger.warn("EachChannelBusinessSheet : The data length exceeds the number of template reservation lines");
            throw new ExcelFormatException("EachChannelBusinessSheet : Please add " + (channelBusinessesDatas.size() - countRow) + " lines of the form template");
        }
        for (int i = 0; i < channelBusinessesDatas.size(); i++) {
            row = sheet.getRow(start.getRow() + i);
            row.getCell(0).setCellValue(channelBusinessesDatas.get(i).getChannel());
            row.getCell(1).setCellValue(channelBusinessesDatas.get(i).getBusinessSuccess());
        }
    }

    /**
     * 分省：省份数目要和表中省份数目一致（查询出来的数据需要做一致性检查），否则将要重新制作表格
     * 上传模板时需要将分省sheet页图表里的红色填充换成默认（重设匹配样式）
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setProvincesSheet(InspectionDaily inspectionDaily, XSSFWorkbook workbook) throws ExcelFormatException {
        // TODO: 2017/8/15 检查数据量一致性；提示用户未发现表的情况
        logger.info("Set Provinces Sheet");
        List<ProvincesBusiness> datas = inspectionDaily.getProvincesBusinesses();
        XSSFSheet sheet = workbook.getSheet(InspectionConstants.SHEET_A4);
        Position start = new Position(0, 0);
        XSSFRow row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !InspectionConstants.SHEET_A4_SIGN.equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("ProvincesSheet : not found table in the template excel");
                throw new ExcelFormatException("ProvincesSheet : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("ProvincesBusinessSheet start at row : " + start.getRow());
        if (datas == null) {
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
        if (datas.size() > countRow) {
            logger.warn("ProvincesSheet : The data length exceeds the number of template reservation lines");
            // 通知用户数据超过模板预留区域
            // 设置导出失败
            throw new ExcelFormatException("ProvincesSheet : Please add " + (datas.size() - countRow) + " lines of the form template");
        }

        XSSFFont normalFont = workbook.createFont();
        normalFont.setFontName("微软雅黑");
        normalFont.setFontHeightInPoints((short) 8);
        Map<String, XSSFRow> rowMap = new HashMap<>();
        for (int i = 0; i < datas.size(); i++) {
            row = sheet.getRow(start.getRow() + i);
            row.getCell(0).setCellValue(datas.get(i).getProvince());
            row.getCell(1).setCellValue(datas.get(i).getTotalBusinessVolume());
            row.getCell(2).setCellValue(datas.get(i).getTransactionFailure());
            for (int j = 0; j < 5; j++) {
                row.getCell(j).getCellStyle().setFont(normalFont);
            }
            rowMap.put(datas.get(i).getProvince(), row);
        }
        datas.sort(((o1, o2) -> {
            return o1.getTransactionFailure() - o2.getTransactionFailure();
        }));
        XSSFCellStyle percentCellStyle = getDefaultXssfCellStyle(workbook);
        percentCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
        XSSFCellStyle cellStyle = getDefaultXssfCellStyle(workbook);
        XSSFFont redFont = workbook.createFont();
        redFont.setFontName("微软雅黑");
        redFont.setFontHeightInPoints((short) 8);
        redFont.setColor((short) 2);
        cellStyle.setFont(redFont);
        percentCellStyle.setFont(redFont);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                if (j >= 3) {
                    rowMap.get(datas.get(i).getProvince()).getCell(j).setCellStyle(percentCellStyle);
                } else {
                    rowMap.get(datas.get(i).getProvince()).getCell(j).setCellStyle(cellStyle);
                }
            }
        }
//        合计行手动书写
//        row = sheet.createRow(start.getRow() + datas.size());
//        setRowCreateCellStyle(row, cellStyle, 5);
//        row = sheet.createRow(start.getRow() + datas.size() + 1);
//        row.createCell(0).setCellValue("合计");
//        row.createCell(1).setCellFormula("SUM(" + CellUtil.getCellVal(1) + (start.getRow() + 1) + ":" + CellUtil.getCellVal(1) + (row.getRowNum() + 1) + ")");
//        row.createCell(2).setCellFormula("SUM(" + CellUtil.getCellVal(2) + (start.getRow() + 1) + ":" + CellUtil.getCellVal(2) + (row.getRowNum() + 1) + ")");
//        row.createCell(3).setCellFormula("(" + CellUtil.getCellVal(1) + (row.getRowNum() + 1) + "-" + CellUtil.getCellVal(2)
//                + (row.getRowNum() + 1) + ")/" + CellUtil.getCellVal(1) + (row.getRowNum() + 1));
//        row.createCell(4).setCellFormula("SUM(" + CellUtil.getCellVal(4) + (start.getRow() + 1) + ":" + CellUtil.getCellVal(4) + (row.getRowNum() + 1) + ")");
//        setRowGetCellStyle(row, cellStyle, 5);

    }

    private void setRowGetCellStyle(XSSFRow row, XSSFCellStyle cellStyle, int cellsNum) {
        for (int i = 0; i < cellsNum; i++) {
            row.getCell(i).setCellStyle(cellStyle);
        }
    }

    /**
     * 分省环比：
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setProvinceDoDSheet(InspectionDaily inspectionDaily, XSSFWorkbook workbook) throws ExcelFormatException {
        logger.info("Set Province DoD Sheet");
        List<ProvincesDoD> datas = inspectionDaily.getProvincesDoD();
        XSSFSheet sheet = workbook.getSheet(InspectionConstants.SHEET_A5);
        Position start = new Position(0, 0);
        XSSFRow row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !InspectionConstants.SHEET_A5_SIGN.equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("ProvinceDoDSheet : not found table in the template excel");
                throw new ExcelFormatException("ProvinceDoDSheet : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("ProvincesDoDSheet start at row : " + start.getRow());
        if (datas == null) {
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
        if (datas.size() > countRow) {
            logger.warn("ProvinceDoDSheet : The data length exceeds the number of template reservation lines");
            throw new ExcelFormatException("ProvinceDoDSheet : Please add " + (datas.size() - countRow) + " lines of the form template");
        }
        for (int i = 0; i < datas.size(); i++) {
            row = sheet.getRow(start.getRow() + i);
            row.getCell(0).setCellValue(datas.get(i).getProvince());
            row.getCell(1).setCellValue(datas.get(i).getBusinessVolume_T_1());
            row.getCell(2).setCellValue(datas.get(i).getBusinessVolume_T());
        }
    }

    /**
     * 交易金额：
     * (其中单价暂且视为表中已有结果，查询时传前端)
     * 存在问题，新增业务后，需考虑扩展，若用此方式易存在对应错误问题
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setTransactionAmountSheet(InspectionDaily inspectionDaily, XSSFWorkbook workbook) throws ExcelFormatException {
        logger.info("Set Transaction Amount Sheet");
        List<TransactionAmount> datas = inspectionDaily.getTransactionAmount();
        XSSFSheet sheet = workbook.getSheet(InspectionConstants.SHEET_A6);
        Position start = new Position(0, 0);
        XSSFRow row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !InspectionConstants.SHEET_A6_SIGN.equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("TransactionAmountSheet : not found table in the template excel");
                throw new ExcelFormatException("TransactionAmountSheet : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("TransactionAmountSheet start at row : " + start.getRow());
        if (datas == null) {
            return;
        }
        XSSFCellStyle cellStyle = sheet.getRow(start.getRow()).getCell(0).getCellStyle();
        for (int i = 0; i < (datas.size() - 1); i++) {
            row = sheet.createRow(start.getRow() + i);
            row.createCell(0).setCellValue(datas.get(i).getProduct());
            row.createCell(1).setCellValue(datas.get(i).getPrice());
            row.createCell(2).setCellValue(datas.get(i).getSingleDayAmount());
//            row.createCell(3).setCellValue(datas.get(i).getTransactionAmount());
            row.createCell(3).setCellFormula(CellUtil.getCellVal(1) + (row.getRowNum() + 1) + "*" + CellUtil.getCellVal(2) + (row.getRowNum() + 1));
            setRowGetCellStyle(row, cellStyle, 4);
        }
        XSSFCellStyle cellStyleAno = getDefaultXssfCellStyle(workbook);
        cellStyleAno.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        cellStyleAno.setFillForegroundColor((short) 47);
//        byte[] bytes = {-3, -23, -39};
//        cellStyleAno.setFillForegroundColor(new XSSFColor(bytes));
        row = sheet.createRow(start.getRow() + datas.size() - 1);
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                row.createCell(i).setCellValue("合计");
            } else {
                row.createCell(i).setCellFormula("SUM(" + CellUtil.getCellVal(i) + (start.getRow() + 1) + ":" + CellUtil.getCellVal(i) + (row.getRowNum()) + ")");
            }
            row.getCell(i).setCellStyle(cellStyleAno);
        }
    }

    private XSSFCellStyle getDefaultXssfCellStyle(XSSFWorkbook workbook) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        XSSFFont font = workbook.createFont();
        font.setFontName("微软雅黑");
        font.setFontHeightInPoints((short) 8);
        cellStyle.setWrapText(true);
        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * 各APP销售情况：
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setEachAPPSale(InspectionDaily inspectionDaily, XSSFWorkbook workbook) throws ExcelFormatException {
        logger.info("Set Each APP Sale");
        Map<String, EachAPPSale> eachAPPSale = inspectionDaily.getEachAPPSale();
        eachAPPSale.forEach((k1, v1) -> {
            v1.getProducts().forEach((k2, v2) -> {
                v1.setTotal(v1.getTotal() + v2);
            });
        });
        /**
         * 查出来的数据必须要按照总量进行排序
         */
        APPSaleMapComparator appSaleMapComparator = new APPSaleMapComparator(eachAPPSale);
        TreeMap<String, EachAPPSale> sortedMap = new TreeMap<>(appSaleMapComparator);
        // TODO: 2017/8/22  inspectionDaily.setEachAPPSale(sortedMap); 返回给前端的数据需进行排序
        XSSFSheet sheet = workbook.getSheet(InspectionConstants.SHEET_D2);
        Position start = new Position(0, 0);
        XSSFRow row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !InspectionConstants.SHEET_D2_SIGN.equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("EachAPPSale : not found table in the template excel");
                throw new ExcelFormatException("EachAPPSale : The table start flag was not found within" + InspectionConstants.SAFE_LINE + " lines");
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);//"价格分类单元格占两行"
        start.setCol(start.getCol());
        logger.info("EachAPPSaleSheet start at row : " + start.getRow());
        if (sortedMap == null) {
            return;
        }
        for (EachAPPSale appSale : sortedMap.values()) {

        }
        //检查结束行，与数据量对比
        int countRow = 0;
        while (countRow < InspectionConstants.SAFE_LINE) {
            row = sheet.getRow(start.getRow() + countRow);
            if (row == null || row.getCell(0) == null || "".equals(row.getCell(0).toString()) || "合计".equals(row.getCell(0).toString()) || "总计".equals(row.getCell(0).toString())) {
                break;
            }
            countRow++;
        }
        if (sortedMap.size() > countRow) {
            logger.warn("ProvinceDoDSheet : The data length exceeds the number of template reservation lines");
            throw new ExcelFormatException("ProvinceDoDSheet : Please add " + (sortedMap.size() - countRow) + " lines of the form template");
        }
        sortedMap.forEach((k, v) -> {

        });
        sortedMap.forEach((k, v) -> {
            sheet.getRow(start.getRow()).getCell(start.getCol()).setCellValue(k);
            sheet.getRow(start.getRow() + 1).getCell(start.getCol()).setCellValue(k);
            start.setCol(start.getCol() + 1);
        });
    }

    private void closeOutputStream(OutputStream os) {
        logger.info("Close outputStream");
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                logger.error("Close OutputStream Error");
                e.printStackTrace();
            }
        }
    }

    private void serResponse(HttpServletResponse response) throws UnsupportedEncodingException {
        logger.info("Set HttpServletResponse");
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-Disposition", "attachment; filename="
                + URLEncoder.encode(InspectionConstants.FILE_NAME, "UTF-8"));
        //设置下载进度
        //response.setHeader("Content-Length", "518KB");//如果设置必须与文件大小完全一致，短则截断，长则超时。
    }

}
