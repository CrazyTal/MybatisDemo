package tk.mybatis.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.springboot.model.InspectionDaily;
import tk.mybatis.springboot.service.InspectionCheckService;
import tk.mybatis.springboot.service.InspectionDataService;
import tk.mybatis.springboot.service.InspectionExportService;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class InspectionController {
    private Logger logger = LoggerFactory.getLogger(InspectionController.class);

    @Autowired
    private InspectionExportService inspectionExportService;
    @Autowired
    private InspectionCheckService inspectionCheckService;
    @Autowired
    private InspectionDataService inspectionDataService;

    /**
     * 导出巡检日报
     *
     * @param response
     * @param type     日报类型  eg. type = "totalBusinessVolume";
     * @param date     日期          eg. queryDate = "2017-08-03";
     * @return
     */
    @RequestMapping(value = "/businessInspection/export", method = RequestMethod.GET)
    @ResponseBody
    public void exportExcel(HttpServletResponse response,
                            @RequestParam(value = "type", required = true) String type,
                            @RequestParam(value = "date", required = true) String date) {
        try {
            logger.info("Export Excel Start ...");

            inspectionExportService.exportExcel(response, type, date);

            logger.info("Export Excel End !");
        } catch (Exception e) {
            logger.error("Export Excel Failed !!!", e);
            e.printStackTrace();
        }
    }

    /**
     * 校验导出
     *
     * @param type 日报类型  eg. type = "totalBusinessVolume";
     * @param date 日期          eg. queryDate = "2017-08-03";
     * @return
     */
    @RequestMapping(value = "/businessInspection/checkExport", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> checkExport(
            @RequestParam(value = "type", required = true) String type,
            @RequestParam(value = "date", required = true) String date) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            logger.info("Check Export Start ...");

            inspectionCheckService.checkExport(type, date, resultMap);

            logger.info("Check Export end !");
        } catch (Exception e) {
            resultMap.put("result", "failed");
            resultMap.put("message", "系统异常");
            logger.error("Check Export Failed !!!", e);
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 查询巡检日报数据
     *
     * @param type 日报类型  eg. type = "totalBusinessVolume";
     * @param date 日期          eg. queryDate = "2017-08-03";
     * @return
     */
    @RequestMapping(value = "/businessInspection/query", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> queryInspectionData(
            @RequestParam(value = "type", required = true) String type,
            @RequestParam(value = "date", required = true) String date) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            logger.info("Query Inspection Data Start ...");

            InspectionDaily data = inspectionDataService.queryInspectionData(resultMap, type, date);

            //为解决从缓存拿出数据无提示情况
            if (data == null) {
                resultMap.put("result", "failed");
                resultMap.put("message", "无数据");
            } else {
                resultMap.put("result", "success");
                resultMap.put("data", data);
            }
            logger.info("Query Inspection Data End !");
        } catch (Exception e) {
            resultMap.put("result", "failed");
            resultMap.put("message", "系统异常");
            logger.error("Query Inspection Data Failed !!!", e);
            e.printStackTrace();
        }

        return resultMap;
    }


    @ModelAttribute
    public Model queryData() {
        logger.info("Enter the Business Inspection");
        return null;
    }

}
