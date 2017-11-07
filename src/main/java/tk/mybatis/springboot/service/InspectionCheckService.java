package tk.mybatis.springboot.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.springboot.exception.ExcelFormatException;
import tk.mybatis.springboot.model.*;
import tk.mybatis.springboot.util.FileUtil;
import tk.mybatis.springboot.util.InspectionConstants;
import tk.mybatis.springboot.util.PropertiesUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class InspectionCheckService {
    private Logger logger = LoggerFactory.getLogger(InspectionCheckService.class);

    @Autowired
    InspectionDataService inspectionDataService;

    /**
     * 校验导出
     *
     * @param type      日报类型
     * @param dateStr   日期
     * @param resultMap 提示信息
     */
    public void checkExport(String type, String dateStr, Map<String, Object> resultMap) throws IOException {
        InspectionDaily inspectionDaily = inspectionDataService.queryInspectionData(resultMap, type, dateStr);
        String dailyType = getDailyType(type, resultMap);
        if (dailyType == null) return;
        String templateName = getTemplateName(resultMap, dailyType);
        if (templateName == null) return;
        if (checkData(inspectionDaily, resultMap)) return;
        try {
            InputStream is = new FileInputStream(new File(templateName));
            Workbook workbook = FileUtil.getWorkbook(is, templateName);
            checkDailyByType(inspectionDaily, workbook, dailyType);
        } catch (ExcelFormatException e) {
            resultMap.put("result", "failed");
            resultMap.put("message", e.getMessage());
            logger.warn("ExcelFormatException : " + e.getMessage());
            return;
        } catch (IOException e) {
            logger.error("Export Excel IOException !");
            throw new IOException("Export Excel IOException", e);
        }
        logger.info("Check InspectionDaily Export Success !");
        resultMap.put("result", "success");
    }

    /**
     * 按类型校验
     *
     * @param inspectionDaily 数据
     * @param workbook        workbook
     * @param dailyType       类型
     * @throws ExcelFormatException
     */
    private void checkDailyByType(InspectionDaily inspectionDaily, Workbook workbook, String dailyType)
            throws ExcelFormatException {
        logger.info("Check " + dailyType);
        switch (dailyType) {
            case "总业务量巡检日报":
                checkTotalDaily(inspectionDaily, workbook, dailyType);
                break;
            case "卓望巡检日报":
                checkZwDaily(inspectionDaily, workbook, dailyType);
                break;
            case "任我购巡检日报":
                checkRwgDaily(inspectionDaily, workbook, dailyType);
                break;
            case "任我看巡检日报":
                checkRwkDaily(inspectionDaily, workbook, dailyType);
                break;
        }
    }

    /**
     * 校验总体情况日报
     *
     * @param inspectionDaily 数据
     * @param workbook        workbook
     * @param dailyType       类型
     * @throws ExcelFormatException
     */
    private void checkTotalDaily(InspectionDaily inspectionDaily, Workbook workbook, String dailyType) throws ExcelFormatException {
        checkTotalBusinessSheet(inspectionDaily, workbook, dailyType, "sheet_a1", "sheet_a1_sign");
        checkDataRechargeSheet(inspectionDaily, workbook, dailyType, "sheet_a2", "sheet_a2_sign");
        checkProductsSheet(inspectionDaily, workbook, dailyType, "sheet_a3", "sheet_a3_1_sign");
        checkChannelsSheet(inspectionDaily, workbook, dailyType, "sheet_a3", "sheet_a3_2_sign");
        checkProvincesSheet(inspectionDaily, workbook, dailyType, "sheet_a4", "sheet_a4_sign");
        checkProvinceDoDSheet(inspectionDaily, workbook, dailyType, "sheet_a5", "sheet_a5_sign");
        CheckTransactionAmountSheet(inspectionDaily, workbook, dailyType, "sheet_a6", "sheet_a6_sign");
        checkDailyContentSheet(workbook, dailyType, "sheet_a7");
    }

    /**
     * 校验卓望日报
     *
     * @param inspectionDaily 数据
     * @param workbook        workbook
     * @param dailyType       类型
     * @throws ExcelFormatException
     */
    private void checkZwDaily(InspectionDaily inspectionDaily, Workbook workbook, String dailyType) throws ExcelFormatException {
        checkTotalBusinessSheet(inspectionDaily, workbook, dailyType, "sheet_b1", "sheet_b1_sign");
        checkProvincesSheet(inspectionDaily, workbook, dailyType, "sheet_b2", "sheet_b2_sign");
        checkProvinceDoDSheet(inspectionDaily, workbook, dailyType, "sheet_b3", "sheet_b3_sign");
        CheckTransactionAmountSheet(inspectionDaily, workbook, dailyType, "sheet_b4", "sheet_b4_sign");
        checkDailyContentSheet(workbook, dailyType, "sheet_b5");
    }

    /**
     * 校验任我购日报
     *
     * @param inspectionDaily 数据
     * @param workbook        workbook
     * @param dailyType       类型
     * @throws ExcelFormatException
     */
    private void checkRwgDaily(InspectionDaily inspectionDaily, Workbook workbook, String dailyType) throws ExcelFormatException {
        checkTotalBusinessSheet(inspectionDaily, workbook, dailyType, "sheet_c1", "sheet_c1_sign");
        checkProvincesSheet(inspectionDaily, workbook, dailyType, "sheet_c2", "sheet_c2_sign");
        checkProductsSheet(inspectionDaily, workbook, dailyType, "sheet_c3", "sheet_c3_1_sign");
        checkChannelsSheet(inspectionDaily, workbook, dailyType, "sheet_c3", "sheet_c3_2_sign");
        CheckTransactionAmountSheet(inspectionDaily, workbook, dailyType, "sheet_c4", "sheet_c4_sign");
        checkDailyContentSheet(workbook, dailyType, "sheet_c5");
    }

    /**
     * 校验任我看日报
     *
     * @param inspectionDaily 数据
     * @param workbook        workbook
     * @param dailyType       类型
     * @throws ExcelFormatException
     */
    private void checkRwkDaily(InspectionDaily inspectionDaily, Workbook workbook, String dailyType) throws ExcelFormatException {
        checkTotalBusinessSheet(inspectionDaily, workbook, dailyType, "sheet_d1", "sheet_d1_sign");
        checkEachAppSheet(inspectionDaily, workbook, dailyType, "sheet_d2", "sheet_d2_sign");
        checkProvincesSheet(inspectionDaily, workbook, dailyType, "sheet_d3", "sheet_d3_sign");
        checkProvinceDoDSheet(inspectionDaily, workbook, dailyType, "sheet_d4", "sheet_d4_sign");
        CheckTransactionAmountSheet(inspectionDaily, workbook, dailyType, "sheet_d5", "sheet_d5_sign");
        checkDailyContentSheet(workbook, dailyType, "sheet_d6");
    }

    /**
     * 校验各APP sheet
     *
     * @param inspectionDaily 数据
     * @param workbook        workbook
     * @param dailyType       类型
     * @param sheetVal        sheet对应值
     * @param startSign       起始标志
     * @throws ExcelFormatException
     */
    private void checkEachAppSheet(InspectionDaily inspectionDaily, Workbook workbook, String dailyType,
                                   String sheetVal, String startSign) throws ExcelFormatException {
        Map<String, EachAPPSale> eachAPPSaleMap = inspectionDaily.getEachAPPSale();
        if (eachAPPSaleMap != null) {
            logger.info("Check Each APP Sale");
            Map<String, EachAPPSale> eachAPPSale = new LinkedHashMap<>(eachAPPSaleMap.size() - 1);
            eachAPPSaleMap.entrySet().stream()
                    .filter(
                            appMap -> !InspectionConstants.TOTAL.equals(appMap.getKey())
                    ).forEach(
                    appMap -> {
                        eachAPPSale.put(appMap.getKey(), appMap.getValue());
                    });
            Set<String> productSet = eachAPPSaleMap.get(InspectionConstants.TOTAL).getProducts().keySet();
            String sheetName = PropertiesUtil.getStrVal(sheetVal);
            Sheet sheet = getSheet(workbook, sheetName, dailyType);
            Position start = new Position(0, 0);
            Row row = sheet.getRow(start.getRow());
            int safeCounts = 0;
            while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal(startSign).equals(row.getCell(0).toString())) {
                safeCounts++;
                row = sheet.getRow(start.getRow() + safeCounts);
                if (safeCounts >= InspectionConstants.SAFE_LINE) {
                    logger.warn("Check EachAPPSale : not found table in the template excel");
                    throw new ExcelFormatException("校验各APP销售情况Sheet : " + InspectionConstants.SAFE_LINE + " 行以内，未找到表格起始标志");
                }
            }
            start.setRow(start.getRow() + safeCounts + 1);
            //检查结束行，与数据量对比
            int countRow = 0;
            while (countRow < InspectionConstants.SAFE_LINE) {
                row = sheet.getRow(start.getRow() + 1 + countRow);//起始行在前一格
                if (row == null || row.getCell(0) == null || "".equals(row.getCell(0).toString()) || "合计".equals(row.getCell(0).toString()) || "总计".equals(row.getCell(0).toString())) {
                    break;
                }
                countRow++;
            }
            if (productSet.size() != countRow) {
                logger.warn("EachAPPSale : The Number of products " +
                        (productSet.size() > countRow ? "exceeds" : "is less than") + " the number of template reservation Row");
                throw new ExcelFormatException("校验各APP销售情况Sheet : 各产品包括 " + productSet + ", 请在模板表格中 " +
                        (productSet.size() > countRow ? "增加 " : "删除 ") + Math.abs(productSet.size() - countRow) + " 行");
            }
            //检查结束列，与数据量对比
            int countCol = 0;
            row = sheet.getRow(start.getRow());
            Cell cell;
            while (countCol < InspectionConstants.SAFE_LINE) {
                cell = row.getCell(start.getCol() + 1 + countCol);
                if (cell == null || "".equals(cell.toString()) || "合计".equals(cell.toString())) {
                    break;
                }
                countCol++;
            }
            if (eachAPPSale.size() > countCol) {
                logger.warn("EachAPPSale : The Number of APPs " +
                        (eachAPPSale.size() > countCol ? "exceeds" : "is less than") + " the number of template reservation Col");
                throw new ExcelFormatException("校验各APP销售情况Sheet : 各APP包括 " + eachAPPSale.keySet() + ", 请在模板表格中 " +
                        (eachAPPSale.size() > countCol ? "增加 " : "删除 ") + Math.abs(eachAPPSale.size() - countCol) + " 列");
            }
        }
    }

    /**
     * 校验日报内容sheet
     *
     * @param workbook  workbook
     * @param dailyType 类型
     * @param sheetVal  sheet对应值
     * @throws ExcelFormatException
     */
    private void checkDailyContentSheet(Workbook workbook, String dailyType, String sheetVal) throws ExcelFormatException {
        logger.info("Check Daily Content Sheet");
        String sheetName = PropertiesUtil.getStrVal(sheetVal);
        getSheet(workbook, sheetName, dailyType);
    }

    /**
     * 校验交易金额sheet
     *
     * @param inspectionDaily 数据
     * @param workbook        workbook
     * @param dailyType       类型
     * @param sheetVal        sheet对应值
     * @param startSign       起始标志
     * @throws ExcelFormatException
     */
    private void CheckTransactionAmountSheet(InspectionDaily inspectionDaily, Workbook workbook, String dailyType,
                                             String sheetVal, String startSign) throws ExcelFormatException {
        List<TransactionAmount> transactionAmountList = inspectionDaily.getTransactionAmount();
        if (transactionAmountList != null) {
            logger.info("Check TransactionAmount Sheet");
            String sheetName = PropertiesUtil.getStrVal(sheetVal);
            Sheet sheet = getSheet(workbook, sheetName, dailyType);
            Position start = new Position(0, 0);
            Row row = sheet.getRow(start.getRow());
            int safeCounts = 0;
            while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal(startSign).equals(row.getCell(0).toString())) {
                safeCounts++;
                row = sheet.getRow(start.getRow() + safeCounts);
                if (safeCounts >= InspectionConstants.SAFE_LINE) {
                    logger.warn("Check TransactionAmountSheet : not found table in the template excel");
                    throw new ExcelFormatException("校验交易金额Sheet : " + InspectionConstants.SAFE_LINE + " 行以内，未找到表格起始标志");
                }
            }
        }
    }

    /**
     * 校验分省环比sheet
     *
     * @param inspectionDaily 数据
     * @param workbook        workbook
     * @param dailyType       类型
     * @param sheetVal        sheet对应值
     * @param startSign       起始标志
     * @throws ExcelFormatException
     */
    private void checkProvinceDoDSheet(InspectionDaily inspectionDaily, Workbook workbook, String dailyType,
                                       String sheetVal, String startSign) throws ExcelFormatException {
        List<ProvincesDoD> provincesDoDList = inspectionDaily.getProvincesDoD();
        if (provincesDoDList != null) {
            logger.info("Check ProvincesDoD Sheet");
            String sheetName = PropertiesUtil.getStrVal(sheetVal);
            Sheet sheet = getSheet(workbook, sheetName, dailyType);
            Position start = new Position(0, 0);
            Row row = sheet.getRow(start.getRow());
            int safeCounts = 0;
            while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal(startSign).equals(row.getCell(0).toString())) {
                safeCounts++;
                row = sheet.getRow(start.getRow() + safeCounts);
                if (safeCounts >= InspectionConstants.SAFE_LINE) {
                    logger.warn("Check ProvinceDoDSheet : not found table in the template excel");
                    throw new ExcelFormatException("校验分省环比Sheet : " + InspectionConstants.SAFE_LINE + " 行以内，未找到表格起始标志");
                }
            }
            start.setRow(start.getRow() + safeCounts + 1);
            //判断结束行，与数据量进行对比
            int countRow = 0;
            while (countRow < InspectionConstants.SAFE_LINE) {
                row = sheet.getRow(start.getRow() + countRow);
                if (row == null || row.getCell(0) == null || "".equals(row.getCell(0).toString()) || "合计".equals(row.getCell(0).toString()) || "总计".equals(row.getCell(0).toString())) {
                    break;
                }
                countRow++;
            }
            if (provincesDoDList.size() > countRow) {
                logger.warn("ProvinceDoDSheet : The data length exceeds the number of template reservation lines");
                throw new ExcelFormatException("校验分省环比Sheet : 请在模板表格中增加 " + (provincesDoDList.size() - countRow) + " 行");
            }
        }
    }

    /**
     * 校验分省sheet
     *
     * @param inspectionDaily
     * @param workbook
     * @param dailyType
     * @param sheetVal
     * @param startsign
     * @throws ExcelFormatException
     */
    private void checkProvincesSheet(InspectionDaily inspectionDaily, Workbook workbook, String dailyType,
                                     String sheetVal, String startsign) throws ExcelFormatException {
        List<ProvincesBusiness> provincesBusinessList = inspectionDaily.getProvincesBusinesses();
        if (provincesBusinessList != null) {
            logger.info("Check ProvincesBusiness Sheet");
            String sheetName = PropertiesUtil.getStrVal(sheetVal);
            Sheet sheet = getSheet(workbook, sheetName, dailyType);
            Position start = new Position(0, 0);
            Row row = sheet.getRow(start.getRow());
            int safeCounts = 0;
            while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal(startsign).equals(row.getCell(0).toString())) {
                safeCounts++;
                row = sheet.getRow(start.getRow() + safeCounts);
                if (safeCounts >= InspectionConstants.SAFE_LINE) {
                    logger.warn("Check ProvincesSheet : not found table in the template excel");
                    throw new ExcelFormatException("校验分省Sheet : " + InspectionConstants.SAFE_LINE + " 行以内，未找到表格起始标志");
                }
            }
            start.setRow(start.getRow() + safeCounts + 1);
            //判断结束行，与数据量进行对比
            int countRow = 0;
            while (countRow < InspectionConstants.SAFE_LINE) {
                row = sheet.getRow(start.getRow() + countRow);
                if (row == null || row.getCell(0) == null || "".equals(row.getCell(0).toString()) || "合计".equals(row.getCell(0).toString()) || "总计".equals(row.getCell(0).toString())) {
                    break;
                }
                countRow++;
            }
            //通知用户数据超过模板预留区域
            if (provincesBusinessList.size() > countRow) {
                logger.warn("Check ProvincesSheet : The data length exceeds the number of template reservation lines");
                throw new ExcelFormatException("校验分省Sheet : 请在模板表格中增加 " + (provincesBusinessList.size() - countRow) + " 行");
            }
        }
    }

    /**
     * 校验分渠道sheet
     *
     * @param inspectionDaily 数据
     * @param workbook        workbook
     * @param dailyType       类型
     * @param sheetVal        sheet对应值
     * @param startSign       起始标志
     * @throws ExcelFormatException
     */
    private void checkChannelsSheet(InspectionDaily inspectionDaily, Workbook workbook, String dailyType,
                                    String sheetVal, String startSign) throws ExcelFormatException {
        List<EachChannelBusiness> eachChannelBusinessList = inspectionDaily.getEachChannelBusinessVolume();
        if (eachChannelBusinessList != null) {
            logger.info("Check EachChannelBusiness Sheet");
            String sheetName = PropertiesUtil.getStrVal(sheetVal);
            Sheet sheet = getSheet(workbook, sheetName, dailyType);
            Position start = new Position(0, 0);
            Row row = sheet.getRow(start.getRow());
            int safeCounts = 0;
            while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal(startSign).equals(row.getCell(0).toString())) {
                safeCounts++;
                row = sheet.getRow(start.getRow() + safeCounts);
                if (safeCounts >= InspectionConstants.SAFE_LINE) {
                    logger.warn("Check EachChannelBusinessSheet : not found table in the template excel");
                    throw new ExcelFormatException("校验分渠道Sheet : " + InspectionConstants.SAFE_LINE + " 行以内，未找到表格起始标志");
                }
            }
            start.setRow(start.getRow() + safeCounts + 1);
            //判断结束行，与数据量进行对比
            int countRow = 0;
            while (countRow < InspectionConstants.SAFE_LINE) {
                row = sheet.getRow(start.getRow() + countRow);
                if (row == null || row.getCell(0) == null || "".equals(row.getCell(0).toString()) || "合计".equals(row.getCell(0).toString()) || "总计".equals(row.getCell(0).toString())) {
                    break;
                }
                countRow++;
            }
            if (eachChannelBusinessList.size() > countRow) {
                logger.warn("Check EachChannelBusinessSheet : The data length exceeds the number of template reservation lines");
                throw new ExcelFormatException("校验分渠道Sheet : 请在模板表格中增加 " + (eachChannelBusinessList.size() - countRow) + " 行");
            }
        }
    }

    /**
     * 校验分产品sheet
     *
     * @param inspectionDaily 数据
     * @param workbook        workbook
     * @param dailyType       类型
     * @param sheetVal        sheet对应值
     * @param startSign       起始标志
     * @throws ExcelFormatException
     */
    private void checkProductsSheet(InspectionDaily inspectionDaily, Workbook workbook, String dailyType,
                                    String sheetVal, String startSign) throws ExcelFormatException {
        List<EachProductBusiness> eachProductBusinessList = inspectionDaily.getEachProductBusinessVolume();
        if (eachProductBusinessList != null) {
            logger.info("Check EachProductBusiness Sheet");
            String sheetName = PropertiesUtil.getStrVal(sheetVal);
            Sheet sheet = getSheet(workbook, sheetName, dailyType);
            Position start = new Position(0, 0);
            Row row = sheet.getRow(start.getRow());
            int safeCounts = 0;
            while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal(startSign).equals(row.getCell(0).toString())) {
                safeCounts++;
                row = sheet.getRow(start.getRow() + safeCounts);
                if (safeCounts >= InspectionConstants.SAFE_LINE) {
                    logger.warn("Check EachProductBusinessSheet : not found table in the template excel");
                    throw new ExcelFormatException("校验分产品Sheet : " + InspectionConstants.SAFE_LINE + " 行以内，未找到表格起始标志");
                }
            }
            start.setRow(start.getRow() + safeCounts + 1);
            //判断结束行，与数据量进行对比
            int countRow = 0;
            while (countRow < InspectionConstants.SAFE_LINE) {
                row = sheet.getRow(start.getRow() + countRow);
                if (row == null || row.getCell(0) == null || "".equals(row.getCell(0).toString()) || "合计".equals(row.getCell(0).toString()) || "总计".equals(row.getCell(0).toString())) {
                    break;
                }
                countRow++;
            }
            if (eachProductBusinessList.size() > countRow) {
                logger.warn("EachProductBusinessSheet : The data length exceeds the number of template reservation lines");
                throw new ExcelFormatException("校验分产品Sheet : 请在模板表格中增加 " + (eachProductBusinessList.size() - countRow) + " 行");
            }
        }
    }

    /**
     * 校验流量充值sheet
     *
     * @param inspectionDaily 数据
     * @param workbook        workbook
     * @param dailyType       类型
     * @param sheetVal        sheet对应值
     * @param startSign       起始标志
     * @throws ExcelFormatException
     */
    private void checkDataRechargeSheet(InspectionDaily inspectionDaily, Workbook workbook, String dailyType,
                                        String sheetVal, String startSign) throws ExcelFormatException {
        List<DataRecharge> dataRechargeList = inspectionDaily.getDataRecharge();
        if (dataRechargeList != null) {
            logger.info("Check DataRecharge Sheet");
            String sheetName = PropertiesUtil.getStrVal(sheetVal);
            Sheet sheet = getSheet(workbook, sheetName, dailyType);
            Position start = new Position(0, 0);
            Row row = sheet.getRow(start.getRow());
            int safeCounts = 0;
            while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal(startSign).equals(row.getCell(0).toString())) {
                safeCounts++;
                row = sheet.getRow(start.getRow() + safeCounts);
                if (safeCounts >= InspectionConstants.SAFE_LINE) {
                    logger.warn("Check DataRechargeSheet : not found table in the template excel");
                    throw new ExcelFormatException("校验流量充值Sheet : " + InspectionConstants.SAFE_LINE + " 行以内，未找到表格起始标志");
                }
            }
        }
    }

    /**
     * 校验总业务量sheet
     *
     * @param inspectionDaily 数据
     * @param workbook        workbook
     * @param dailyType       类型
     * @param sheetVal        sheet对应值
     * @param startSign       起始标志
     * @throws ExcelFormatException
     */
    private void checkTotalBusinessSheet(InspectionDaily inspectionDaily, Workbook workbook, String dailyType,
                                         String sheetVal, String startSign) throws ExcelFormatException {
        List<BusinessVolume> businessVolumeList = inspectionDaily.getTotalBusinessVolume();
        if (businessVolumeList != null) {
            logger.info("Check BusinessVolume Sheet");
            String sheetName = PropertiesUtil.getStrVal(sheetVal);
            Sheet sheet = getSheet(workbook, sheetName, dailyType);
            Position start = new Position(0, 0);
            Row row = sheet.getRow(start.getRow());
            int safeCounts = 0;
            while (row == null || row.getCell(0) == null || !PropertiesUtil.getStrVal(startSign).equals(row.getCell(0).toString())) {
                safeCounts++;
                row = sheet.getRow(start.getRow() + safeCounts);
                if (safeCounts >= InspectionConstants.SAFE_LINE) {
                    logger.warn("Check BusinessVolumeSheet : not found table in the template excel");
                    throw new ExcelFormatException("校验总体情况Sheet : " + InspectionConstants.SAFE_LINE + " 行以内，未找到表格起始标志");
                }
            }
        } else {
            logger.warn("Check BusinessVolumeSheet : TotalBusinessVolume Data Can not Be Null");
            throw new ExcelFormatException("校验总体情况Sheet : 总体情况数据为空");
        }
    }

    /**
     * 获取sheet
     *
     * @param workbook  workbook
     * @param sheetName sheet名
     * @param dailyType 类型
     * @return
     * @throws ExcelFormatException
     */
    private Sheet getSheet(Workbook workbook, String sheetName, String dailyType) throws ExcelFormatException {
        Sheet sheet;
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            logger.warn(dailyType + " : 未找到sheet : " + sheetName);
            throw new ExcelFormatException(dailyType + " : 未找到sheet : " + sheetName);
        }
        return sheet;
    }

    /**
     * 获取模板名称
     *
     * @param resultMap 提示信息
     * @param dailyType 日报类型
     * @return
     */
    private String getTemplateName(Map<String, Object> resultMap, String dailyType) {
        String templateName = "";
        String path = System.getProperty("catalina.home") + "/webapps/template/";
        File file = new File(path);
        if (file.list() == null) {
            resultMap.put("result", "failed");
            resultMap.put("message", "模板路径不存在，请联系开发或运维人员");
            return null;
        }
        for (String f : file.list()) {
            if (f.contains(dailyType)) {
                templateName = f;
                break;
            }
        }
        if (templateName == "") {
            resultMap.put("result", "failed");
            resultMap.put("message", "无模板，请导入");
            return null;
        }
        logger.info("Inspection Daily TemplatePath : " + path);
        logger.info("Inspection Daily TemplateName : " + templateName);
        templateName = path + templateName;
        return templateName;
    }

    /**
     * 获取日报中文名类型，用来查找模板
     *
     * @param type      日报类型
     * @param resultMap 提示信息
     * @return
     */
    private String getDailyType(String type, Map<String, Object> resultMap) {
        String dailyType = null;
        switch (type) {
            case "totalBusinessVolume":
                dailyType = "总业务量巡检日报";
                break;
            case "zhuowang":
                dailyType = "卓望巡检日报";
                break;
            case "renwogou":
                dailyType = "任我购巡检日报";
                break;
            case "renwokan":
                dailyType = "任我看巡检日报";
                break;
        }
        logger.info("Inspection Daily type : " + dailyType);
        if (dailyType == null) {
            resultMap.put("result", "failed");
            resultMap.put("message", "日报类型错误");
            logger.info("日报类型错误 : " + type);
            return null;
        }
        return dailyType;
    }

    /**
     * 判空
     *
     * @param inspectionDaily 日报数据
     * @param resultMap       提示信息
     * @return
     */
    private boolean checkData(InspectionDaily inspectionDaily, Map<String, Object> resultMap) {
        if (inspectionDaily.getTotalBusinessVolume() == null && inspectionDaily.getDataRecharge() == null
                && inspectionDaily.getEachChannelBusinessVolume() == null && inspectionDaily.getEachProductBusinessVolume() == null
                && inspectionDaily.getProvincesBusinesses() == null && inspectionDaily.getProvincesDoD() == null
                && inspectionDaily.getTransactionAmount() == null) {
            //EachAPPSale不可能为Null
            // && inspectionDaily.getEachAPPSale() == null
            resultMap.put("result", "failed");
            resultMap.put("message", "无数据");
            logger.warn("No data found !");
            return true;
        }
        return false;
    }
}
