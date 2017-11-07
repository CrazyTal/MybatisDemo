package tk.mybatis.springboot.service;

import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.springboot.exception.ExcelFormatException;
import tk.mybatis.springboot.model.InspectionDaily;
import tk.mybatis.springboot.util.FileUtil;
import tk.mybatis.springboot.util.InspectionConstants;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class InspectionExportService {

    private Logger logger = LoggerFactory.getLogger(InspectionExportService.class);

    @Autowired
    private TotalBusinessService totalBusinessService;
    @Autowired
    private ZWInspectionService zwInspectionService;
    @Autowired
    private RWGInspectionService rwgInspectionService;
    @Autowired
    private RWKInspectionService rwkInspectionService;
    @Autowired
    private InspectionDataService inspectionDataService;

    /**
     * 导出日报入口
     *
     * @param response
     * @param type     日报类型
     * @param dateStr  日期
     * @throws IOException
     */
    public void exportExcel(HttpServletResponse response, String type, String dateStr) throws IOException {

        InspectionDaily inspectionDaily = inspectionDataService.queryInspectionData(new HashedMap(), type, dateStr);
        try {
            String dailyType = getDailyType(type);
            if (dailyType == null) return;
            String templateName = getTemplateName(dailyType);
            if (templateName == null) return;
            LocalDate date = getLocalDate(dateStr);
            String fileName = getFileName(date, dailyType, templateName);

            Workbook workbook = generateWorkbook(inspectionDaily, dailyType, date, templateName);
            writeExcel(response, fileName, workbook);
        } catch (IOException e) {
            logger.error("Export Excel IOException !");
            throw new IOException("Export Excel IOException", e);
        }
    }

    /**
     * 获取模板名称
     *
     * @param dailyType 日报类型
     * @return
     */
    private String getTemplateName(String dailyType) {
        String templateName = "";
        String path = System.getProperty("catalina.home") + "/webapps/template/";
        for (String f : new File(path).list()) {
            if (f.contains(dailyType)) {
                templateName = f;
                break;
            }
        }
        logger.info("Inspection Daily TemplatePath : " + path);
        logger.info("Inspection Daily TemplateName : " + templateName);
        templateName = path + templateName;
        return templateName;
    }

    /**
     * 写出excel
     *
     * @param response
     * @param fileName 日报name
     * @param workbook poi的workbook
     * @throws IOException
     */
    private void writeExcel(HttpServletResponse response, String fileName, Workbook workbook) throws IOException {
        OutputStream os = null;
        try {
            if (workbook != null) {
                logger.info("Inspection writeExcel Start !");
                serResponse(response, fileName);
                os = response.getOutputStream();
                workbook.write(os);
                logger.info("Inspection writeExcel End !");
            } else {
                logger.error("Inspection Export Failed : Workbook is null");
                throw new IOException("Workbook is null");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            closeOutputStream(os);
        }
    }

    /**
     * 格式时间
     *
     * @param dateStr
     * @return
     */
    private LocalDate getLocalDate(String dateStr) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 生成poi的workbook
     *
     * @param inspectionDaily 日报数据
     * @param dailyType       日报类型
     * @param date            日期
     * @param templateName    模板名称
     * @return
     * @throws IOException
     */
    private Workbook generateWorkbook(InspectionDaily inspectionDaily, String dailyType, LocalDate date,
                                      String templateName) throws IOException {

        InputStream is = new FileInputStream(new File(templateName));
        Workbook workbook = getWorkbook(templateName, is);
        if (workbook == null) return null;
        try {
            switch (dailyType) {
                case InspectionConstants.TOTALBUSINESS:
                    totalBusinessService.exportTotalBusinessDaily(inspectionDaily, workbook, date);
                    break;
                case InspectionConstants.ZHUOWANG:
                    zwInspectionService.exportZWDaily(inspectionDaily, workbook, date);
                    break;
                case InspectionConstants.RENWOGOU:
                    rwgInspectionService.exportRWGDaily(inspectionDaily, workbook, date);
                    break;
                case InspectionConstants.RENWOKAN:
                    rwkInspectionService.exportRWKDaily(inspectionDaily, workbook, date);
                    break;
                default:
                    logger.warn("Inspection Export Type is not correct");
                    break;
            }
        } catch (ExcelFormatException e) {
            logger.warn("ExcelFormatException : " + e.getMessage());
            return null;
        }
        logger.info("Inspection Generate Workbook Success");
        return workbook;
    }

    private Workbook getWorkbook(String templateName, InputStream is) throws IOException {
        Workbook workbook = FileUtil.getWorkbook(is, templateName);
        if (workbook == null) {
            logger.error("Inspection Generate Workbook Failed");
            return null;
        }
        return workbook;
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
                && inspectionDaily.getTransactionAmount() == null && inspectionDaily.getEachAPPSale() == null) {
            resultMap.put("result", "failed");
            resultMap.put("message", "no data !");
            logger.warn("No data found !");
            return true;
        }
        return false;
    }

    /**
     * 关闭流
     *
     * @param os
     */
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

    /**
     * 设置response返回类型及消息头
     *
     * @param response
     * @param fileName 导出文件名
     * @throws UnsupportedEncodingException
     */
    private void serResponse(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        logger.info("Set HttpServletResponse");
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-Disposition", "attachment; filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        //设置下载进度
        //response.setHeader("Content-Length", "518KB");//如果设置必须与文件大小完全一致，短则截断，长则超时。
    }

    /**
     * 获取日报中文名类型，用来查找模板
     *
     * @param type 日报类型
     * @return
     */
    private String getDailyType(String type) {
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
        return dailyType;
    }

    /**
     * 组装导出文件name
     *
     * @param date         日期
     * @param dailyType    日报类型（中文）
     * @param templateName 模板名称
     * @return
     */
    private String getFileName(LocalDate date, String dailyType, String templateName) {
        String fileName = "";
        fileName = dailyType + "_" + DateTimeFormatter.ofPattern("yyyyMMdd").format(date)
                + templateName.substring(templateName.lastIndexOf("."));
        logger.info("Inspection Daily Export FileName : " + fileName);
        return fileName;
    }

}
