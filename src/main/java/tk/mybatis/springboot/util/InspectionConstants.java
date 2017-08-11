package tk.mybatis.springboot.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ltao on 2017/7/31.
 */
public interface InspectionConstants {

    String STR_FORMAT = "0000";
    String FILE_NAME = "TestFile.xlsx";
    String SHEET_A1_SIGN = "日期";//表格起始标志可修改为读取xml文件。与起点标志同列且在起点标志之前的所有单元格不可含有与其相同的单元格内容
    String SHEET_A2_SIGN = "日期";
    String SHEET_A3_1_SIGN = "业务分类";
    String SHEET_A3_2_SIGN = "渠道分类";
    String SHEET_A4_SIGN = "省份";
    String SHEET_A5_SIGN = "省份";
    String SHEET_A6_SIGN = "商品名称";

    String SHEET_D2_SIGN = "价格分类";

    String SHEET_A1 = "总体情况";//可修改为配置
    String SHEET_A2 = "流量充值";//可修改为配置
    String SHEET_A3 = "分产品&分渠道";//可修改为配置
    String SHEET_A4 = "分省";//可修改为配置
    String SHEET_A5 = "分省环比";//可修改为配置
    String SHEET_A6 = "交易金额";//可修改为配置
    String SHEET_A7 = "日报内容";//可修改为配置

    String SHEET_D2 = "任我看-各APP销售情况";


    int SAFE_LINE = 1000;

    Map<String, String> PATHMAP = new HashMap<String, String>() {
        {
            put("totalBusinessVolume", "D:\\附录5：总业务量巡检日报_20170706.xlsx");
            put("zhuowang", "D:\\附录6：卓望巡检日报_20170706.xlsx");
            put("renwogou", "D:\\附录7：任我购巡检日报_20170706.xlsx");
            put("renwokan", "D:\\附录8：任我看巡检日报_20170706.xlsx");
        }
    };

//    void empty();
}
