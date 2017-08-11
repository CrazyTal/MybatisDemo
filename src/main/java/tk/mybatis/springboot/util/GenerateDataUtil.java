package tk.mybatis.springboot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.springboot.model.*;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by ltao on 2017/7/26.
 */
public class GenerateDataUtil {

    private static Logger logger = LoggerFactory.getLogger(GenerateDataUtil.class);

    public static List<BusinessVolume> generateExcelData() {

//        long a1 = System.currentTimeMillis();
//        List<BusinessVolume> datas1 = new ArrayList<>(30);
//        for (int i = 0; i < 30; i++) {
//            datas1.add(new BusinessVolume(i + "", 100, 100, 100));
//        }
//        long b1 = System.currentTimeMillis();
//        System.out.println("DecimalFormat time : " + (b1 - a1));
        long a = System.currentTimeMillis();
        List<BusinessVolume> datas = new ArrayList<>(31);
        for (int i = 0; i < 31; i++) {
            datas.add(new BusinessVolume(new DecimalFormat(InspectionConstants.STR_FORMAT).format(i), 200000, 1000, 100));
        }
        long b = System.currentTimeMillis();//使用格式化的数据测试时比普通数据慢2ms
        logger.info("Data DecimalFormat time : " + (b - a));

        return datas;
    }

    /**
     * 此方法为查询数据库返回的日报数据
     *
     * @return 日报数据
     */
    public static InspectionDaily getDatas() {
        InspectionDaily inspectionDaily = new InspectionDaily();//inspectionDaily.findDatas();
        //总体情况
        inspectionDaily.setTotalBusinessVolume(new ArrayList<>(31));
        for (int i = 0; i < 31; i++) {
            inspectionDaily.getTotalBusinessVolume().add(new BusinessVolume(new DecimalFormat(
                    InspectionConstants.STR_FORMAT).format(i), 200000, 1000, 100));
        }
        //流量充值
        inspectionDaily.setDataRecharge(new ArrayList<>(31));
        for (int i = 0; i < 31; i++) {
            inspectionDaily.getDataRecharge().add(new DataRecharge(
                    new DecimalFormat(InspectionConstants.STR_FORMAT).format(i), 200000, 1000));
        }
        //分产品
        inspectionDaily.setEachProductBusinessVolume(new ArrayList<>(8));
        for (int i = 0; i < 8; i++) {
            inspectionDaily.getEachProductBusinessVolume().add(new EachProductBusiness("产品" + i, 100 + i));
        }
        inspectionDaily.getEachProductBusinessVolume().sort((a1, a2) -> {
            return a2.getBusinessSuccess() - a1.getBusinessSuccess();
        });
        //分渠道
        inspectionDaily.setEachChannelBusinessVolume(new ArrayList<>(10));
        for (int i = 0; i < 10; i++) {
            inspectionDaily.getEachChannelBusinessVolume().add(new EachChannelBusiness("渠道" + i, 200 + i));
        }
        inspectionDaily.getEachChannelBusinessVolume().sort((a1, a2) -> {
            return a2.getBusinessSuccess() - a1.getBusinessSuccess();
        });
        //分省
        Set<String> provinces = new HashSet<String>() {{
            add("广东");
            add("四川");
            add("北京");
            add("安徽");
            add("贵州");
            add("广西");
            add("山东");
            add("陕西");
            add("河北");
            add("湖南");
            add("江苏");
            add("上海");
            add("云南");
            add("河南");
            add("重庆");
            add("辽宁");
            add("山西");
            add("吉林");
            add("福建");
            add("内蒙古");
            add("黑龙江");
            add("江西");
            add("天津");
            add("浙江");
            add("海南");
            add("湖北");
            add("甘肃");
            add("宁夏");
            add("新疆");
            add("青海");
            add("西藏");
        }};
        inspectionDaily.setProvincesBusinesses(new ArrayList<>(provinces.size()));
        Iterator<String> iterator = provinces.iterator();
        for (int i = 0; i < provinces.size(); i++) {
            if (iterator.hasNext()) {
                inspectionDaily.getProvincesBusinesses().add(new ProvincesBusiness(iterator.next(), 500 + i * 2, (int) (Math.random() * 10 + 1)));
            }
        }
        inspectionDaily.getProvincesBusinesses().sort((a1, a2) -> {
            return a2.getTotalBusinessVolume() - a1.getTotalBusinessVolume();
        });
        //分省环比
        inspectionDaily.setProvincesDoD(new ArrayList<>(provinces.size()));
        for (int i = 0; i < provinces.size(); i++) {
            if (iterator.hasNext()) {
                inspectionDaily.getProvincesDoD().add(new ProvincesDoD(iterator.next(), 500 + i * 2, 550 + (int) (Math.random() * 10 + 1)));
            }
        }
        inspectionDaily.getProvincesDoD().sort(((o1, o2) -> {
            return o2.getBusinessVolume_T() - o1.getBusinessVolume_T();
        }));
        //交易金额
        inspectionDaily.setTransactionAmount(new ArrayList<>(50));
        for (int i = 0; i < 50; i++) {
            inspectionDaily.getTransactionAmount().add(new TransactionAmount("商品" + i, i + 10, 500, (i + 10) * 500));
        }

        return inspectionDaily;
    }
}
