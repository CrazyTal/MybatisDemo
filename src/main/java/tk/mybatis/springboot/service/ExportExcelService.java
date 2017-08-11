package tk.mybatis.springboot.service;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tk.mybatis.springboot.model.*;
import tk.mybatis.springboot.util.APPSaleMapComparator;
import tk.mybatis.springboot.util.GenerateDataUtil;
import tk.mybatis.springboot.util.InspectionConstants;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by ltao on 2017/7/26.
 */
@Service
public class ExportExcelService {
    private Logger logger = LoggerFactory.getLogger(ExportExcelService.class);

    public void exportExcel(HttpServletResponse response, Date date, String type) {
        OutputStream os = null;
        // TODO: 2017/8/9 findDataByDate
        // TODO: 2017/8/11 日报内容
        /**
         * 当写入ecel值时在最底层留下空格，用代码判断行数是否和数据库读取的数据相同，若是不同则提示用户
         * 交易金额sheet：如果采用全部由数据库读写，则需将excel模板中数据清除，或只留下一条示例记录
         */
        InspectionDaily inspectionDaily = GenerateDataUtil.getDatas();
        try {
            serResponse(response);
            os = response.getOutputStream();
            if (inspectionDaily.getTotalBusinessVolume() == null) {
                /**
                 * 1.无数据报错
                 * 2.无数据导出空表
                 */
                return;
            }
            generateExcel(inspectionDaily, os, type);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeOutputStream(os);
        }

    }

    private void generateExcel(InspectionDaily inspectionDaily, OutputStream os, String type) throws IOException {
        type = "totalBusinessVolume";//后期删除
        logger.info("Excel type : " + type);
        File file = new File(InspectionConstants.PATHMAP.get(type));
        InputStream is = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        switch (type) {
            case "totalBusinessVolume":
                //sheetA1
                setBusinessVolumeSheet(inspectionDaily, workbook);
                setDataRechargeSheet(inspectionDaily, workbook);
                setEachProductAndChannelSheet(inspectionDaily, workbook);
                setProvincesSheet(inspectionDaily, workbook);
                setProvinceDoDSheet(inspectionDaily, workbook);
                setTransactionAmountSheet(inspectionDaily, workbook);
//                setEachAPPSale(inspectionDaily, workbook);//各APP
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
        if (!InspectionConstants.PATHMAP.containsKey(type)) {
            logger.warn("There is no correct Excel template");
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
    private void setBusinessVolumeSheet(InspectionDaily inspectionDaily, XSSFWorkbook workbook) {
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
                logger.warn("not found table in the template excel");
                //log warning || throw Exception
                break;
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
    private void setDataRechargeSheet(InspectionDaily inspectionDaily, XSSFWorkbook workbook) {
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
                logger.warn("not found table in the template excel");
                //log warning || throw Exception
                break;
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
    private void setEachProductAndChannelSheet(InspectionDaily inspectionDaily, XSSFWorkbook workbook) {
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
                logger.warn("not found table in the template excel");
                //log warning || throw Exception
                break;
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("EachProductBusinessSheet start at row : " + start.getRow());
        if (productBusinessesDatas == null) {
            return;
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
                logger.warn("not found table in the template excel");
                //log warning || throw Exception
                break;
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("EachProductBusinessSheet start at row : " + start.getRow());
        if (channelBusinessesDatas == null) {
            return;
        }
        for (int i = 0; i < channelBusinessesDatas.size(); i++) {
            row = sheet.getRow(start.getRow() + i);
            row.getCell(0).setCellValue(channelBusinessesDatas.get(i).getChannel());
            row.getCell(1).setCellValue(channelBusinessesDatas.get(i).getBusinessSuccess());
        }
    }

    /**
     * 分省：省份数目要和表中省份数目一致（查询出来的数据需要做一致性检查），否则将要重新制作表格
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setProvincesSheet(InspectionDaily inspectionDaily, XSSFWorkbook workbook) {
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
                logger.warn("not found table in the template excel");
                //log warning || throw Exception
                break;
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("ProvincesBusinessSheet start at row : " + start.getRow());
        if (datas == null) {
            return;
        }
        for (int i = 0; i < datas.size(); i++) {
            row = sheet.getRow(start.getRow() + i);
            row.getCell(0).setCellValue(datas.get(i).getProvince());
            row.getCell(1).setCellValue(datas.get(i).getTotalBusinessVolume());
            row.getCell(2).setCellValue(datas.get(i).getTransactionFailure());
        }
    }

    /**
     * 分省环比：
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setProvinceDoDSheet(InspectionDaily inspectionDaily, XSSFWorkbook workbook) {
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
                logger.warn("not found table in the template excel");
                //log warning || throw Exception
                break;
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("ProvincesDoDSheet start at row : " + start.getRow());
        if (datas == null) {
            return;
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
    private void setTransactionAmountSheet(InspectionDaily inspectionDaily, XSSFWorkbook workbook) {
        logger.info("Set Transaction Amount Sheet");
        List<TransactionAmount> datas = inspectionDaily.getTransactionAmount();
//        XSSFCellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
//        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
//        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        XSSFSheet sheet = workbook.getSheet(InspectionConstants.SHEET_A6);
        Position start = new Position(0, 0);
        XSSFRow row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !InspectionConstants.SHEET_A6_SIGN.equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("not found table in the template excel");
                //log warning || throw Exception
                break;
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);
        logger.info("TransactionAmountSheet start at row : " + start.getRow());
        if (datas == null) {
            return;
        }
        XSSFCellStyle cellStyle = sheet.getRow(start.getRow()).getCell(0).getCellStyle();
        //todo 添加数据时向最后一行添加了合计
        for (int i = 0; i < datas.size(); i++) {
            row = sheet.createRow(start.getRow() + i);
            row.createCell(0).setCellValue(datas.get(i).getProduct());
            row.createCell(1).setCellValue(datas.get(i).getPrice());
            row.createCell(2).setCellValue(datas.get(i).getSingleDayAmount());
            row.createCell(3).setCellValue(datas.get(i).getSingleDayAmount());
            row.getCell(0).setCellStyle(cellStyle);
            row.getCell(1).setCellStyle(cellStyle);
            row.getCell(2).setCellStyle(cellStyle);
            row.getCell(3).setCellStyle(cellStyle);
        }
    }

    /**
     * 各APP销售情况：
     *
     * @param inspectionDaily
     * @param workbook
     */
    private void setEachAPPSale(InspectionDaily inspectionDaily, XSSFWorkbook workbook) {
        logger.info("Set Each APP Sale");
        Map<String, EachAPPSale> eachAPPSale = inspectionDaily.getEachAPPSale();
        eachAPPSale.forEach((k, v) -> {
            v.setTotal(v.getPrice_9() + v.getPrice_24());
        });
        APPSaleMapComparator appSaleMapComparator = new APPSaleMapComparator(eachAPPSale);
        TreeMap<String, EachAPPSale> sortedMap = new TreeMap<>(appSaleMapComparator);
        /**
         * 查出来的数据必须要按照总量进行排序
         */
        XSSFSheet sheet = workbook.getSheet(InspectionConstants.SHEET_D2);
        Position start = new Position(0, 0);
        XSSFRow row = sheet.getRow(start.getRow());
        int safeCounts = 0;
        while (row == null || row.getCell(0) == null || !InspectionConstants.SHEET_D2_SIGN.equals(row.getCell(0).toString())) {
            safeCounts++;
            row = sheet.getRow(start.getRow() + safeCounts);
            if (safeCounts >= InspectionConstants.SAFE_LINE) {
                logger.warn("not found table in the template excel");
                //log warning || throw Exception
                break;
            }
        }
        start.setRow(start.getRow() + safeCounts + 1);//"价格分类单元格占两行"
        start.setCol(start.getCol() + 1);//此表格竖向写入
        logger.info("EachAPPSaleSheet start at row : " + start.getRow());
        //先进行计算，后根据总数进行排序 //todo 计算需要放在查询数据后处理
        if (sortedMap == null) {
            return;
        }
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
