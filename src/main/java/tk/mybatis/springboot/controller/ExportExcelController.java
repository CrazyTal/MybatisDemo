package tk.mybatis.springboot.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import tk.mybatis.springboot.service.ExportExcelService;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ltao on 2017/7/26.
 */
@Controller
public class ExportExcelController {
    private Logger logger = LoggerFactory.getLogger(ExportExcelController.class);

    @Autowired
    private ExportExcelService service;

    /**
     * @param response
     * @param date
     * @param type
     */
    @GetMapping(value = "/excel")// TODO: 2017/8/21 后期改为POST请求
    public Map<String, Object> writeExcel(HttpServletResponse response, Date date, String type) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            logger.info("Export Excel Start ...");
            type = "totalBusinessVolume";//todo 后期删除
            service.exportExcel(response, date, type, resultMap);
            logger.info("Export Excel End !");
        } catch (Exception e) {
            resultMap.put("result", "failed");
            resultMap.put("message", "系统异常");
            logger.error("Export Excel Failed !!!", e);
            e.printStackTrace();
        }
        return resultMap;
    }


}
