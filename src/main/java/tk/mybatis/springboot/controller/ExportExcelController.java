package tk.mybatis.springboot.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import tk.mybatis.springboot.service.ExportExcelService;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by ltao on 2017/7/26.
 */
@Controller
public class ExportExcelController {
    private Logger logger = LoggerFactory.getLogger(ExportExcelController.class);

    @Autowired
    private ExportExcelService service;

    /**
     *
     * @param response
     * @param date
     * @param type
     */
    @GetMapping(value = "/excel")
    public void writeExcel(HttpServletResponse response, Date date, String type) {

        try {
            logger.info("Export Excel Start ...");
            service.exportExcel(response, date, type);
            logger.info("Export Excel End !");
        } catch (Exception e) {
            logger.error("Export Excel Failed !!!");
            e.printStackTrace();
        }

    }


}
