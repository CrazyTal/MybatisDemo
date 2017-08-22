package tk.mybatis.springboot.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ltao on 2017/8/21.
 */

public class ExcelFormatException extends Exception {

    private Logger logger = LoggerFactory.getLogger(ExcelFormatException.class);

    public ExcelFormatException(String msg) {
        super(msg);
        logger.warn("Excel Format Exception!");
    }
}
