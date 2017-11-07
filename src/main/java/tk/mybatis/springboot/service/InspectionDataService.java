package tk.mybatis.springboot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tk.mybatis.springboot.mapper.InspectionDataDao;
import tk.mybatis.springboot.model.*;
import tk.mybatis.springboot.util.APPSaleMapComparator;
import tk.mybatis.springboot.util.InspectionConstants;

import java.util.*;

@Service
public class InspectionDataService {

    private Logger logger = LoggerFactory.getLogger(InspectionDataService.class);

    @Autowired
    InspectionDataDao inspectionDataDao;

    /**
     * 查询数据入口
     *
     * @param resultMap
     * @param type
     * @param queryDate
     * @return
     */
    @Cacheable(value = "inspectionDaily", key = "#type+#queryDate")
    public InspectionDaily queryInspectionData(Map<String, Object> resultMap, String type, String queryDate) {
        logger.info("Query Inspection Data from Database");
        logger.info("Query Inspection Data Time : " + queryDate);
        List<List<?>> datas = queryDatasByType(type, queryDate);
        logger.info("Query Inspection Data Type : " + type);
        logger.info("Query Inspection Data's Size : " + datas.size());

        InspectionDaily inspectionDaily = getInspectionDailyData(datas);
        if (checkResultData(inspectionDaily)) return null;

        logger.info("Query Inspection Data : success");
        return inspectionDaily;
    }

    /**
     * 检查数据
     *
     * @param inspectionDaily 数据
     * @return
     */
    private boolean checkResultData(InspectionDaily inspectionDaily) {
        //总体情况若无数据，其他也会无数据
        if (inspectionDaily.getTotalBusinessVolume() == null) {
            logger.error("Query Inspection Data : no data");
            return true;
        }
        return false;
    }

    /**
     * 根据类型查找
     *
     * @param type      日报类型
     * @param queryDate 日期
     * @return
     */
    private List<List<?>> queryDatasByType(String type, String queryDate) {
        List<List<?>> datas = null;
        switch (type) {
            case "totalBusinessVolume":
                datas = inspectionDataDao.queryTotalDailyData(queryDate);
                break;
            case "zhuowang":
                datas = inspectionDataDao.queryZwDailyData(queryDate);
                break;
            case "renwogou":
                datas = inspectionDataDao.queryRwgDailyData(queryDate);
                break;
            case "renwokan":
                datas = inspectionDataDao.queryRwkDailyData(queryDate);
                break;
        }
        return datas;
    }

    /**
     * 根据类型组装成巡检日报数据
     *
     * @param datas 源数据
     * @return
     */
    private InspectionDaily getInspectionDailyData(List<List<?>> datas) {
        InspectionDaily inspectionDaily = new InspectionDaily();
        datas.stream().filter(data -> data != null && data.size() > 0).forEach(data -> {
            if (data.get(0) instanceof BusinessVolume) {
                logger.info("Query Inspection Data Contains TotalBusinessVolume Data");
                inspectionDaily.setTotalBusinessVolume((List<BusinessVolume>) data);
            } else if (data.get(0) instanceof DataRecharge) {
                logger.info("Query Inspection Data Contains DataRecharge Data");
                inspectionDaily.setDataRecharge((List<DataRecharge>) data);
            } else if (data.get(0) instanceof EachProductBusiness) {
                logger.info("Query Inspection Data Contains EachProductBusiness Data");
                inspectionDaily.setEachProductBusinessVolume((List<EachProductBusiness>) data);
            } else if (data.get(0) instanceof EachChannelBusiness) {
                logger.info("Query Inspection Data Contains EachChannelBusiness Data");
                inspectionDaily.setEachChannelBusinessVolume((List<EachChannelBusiness>) data);
            } else if (data.get(0) instanceof ProvincesBusiness) {
                logger.info("Query Inspection Data Contains ProvincesBusinesses Data");
                inspectionDaily.setProvincesBusinesses((List<ProvincesBusiness>) data);
            } else if (data.get(0) instanceof ProvincesDoD) {
                logger.info("Query Inspection Data Contains ProvincesDoD Data");
                inspectionDaily.setProvincesDoD((List<ProvincesDoD>) data);
            } else if (data.get(0) instanceof TransactionAmount) {
                logger.info("Query Inspection Data Contains TransactionAmount Data");
                inspectionDaily.setTransactionAmount((List<TransactionAmount>) data);
            } else if (data.get(0) instanceof AppRawData) {
                logger.info("Query Inspection Data Contains EachAPPSale Data");
                Map<String, EachAPPSale> eachAPPSale = getEachAPPSaleData(data);
                inspectionDaily.setEachAPPSale(eachAPPSale);
            }
        });
        return inspectionDaily;
    }

    /**
     * 组装各APP数据
     *
     * @param data 源数据
     * @return
     */
    private Map<String, EachAPPSale> getEachAPPSaleData(List<?> data) {
        List<AppRawData> appRawDataList = (List<AppRawData>) data;
        Map<String, EachAPPSale> eachAPPSale = new LinkedHashMap<String, EachAPPSale>(data.size());
        Map<String, EachAPPSale> originAppMap = dealAppRawData(data, appRawDataList);
        calProductsTotal(originAppMap);
        APPSaleMapComparator appSaleMapComparator = new APPSaleMapComparator(originAppMap);
        TreeMap<String, EachAPPSale> sortedAppMap = new TreeMap<>(appSaleMapComparator);
        sortedAppMap.putAll(originAppMap);
        eachAPPSale.putAll(sortedAppMap);
        EachAPPSale totalAppSale = getTotalAPPSale(sortedAppMap);
        //计算占比包括要计算合计，所以先put进去，再计算
        eachAPPSale.put(InspectionConstants.TOTAL, totalAppSale);
        calAppsTotal(eachAPPSale, totalAppSale);
        return eachAPPSale;
    }

    /**
     * 计算总APP合计
     *
     * @param eachAPPSale  各APP数据
     * @param totalAppSale 合计数据
     */
    private void calAppsTotal(Map<String, EachAPPSale> eachAPPSale, EachAPPSale totalAppSale) {
        int eachAppTotal = totalAppSale.getTotal();
        eachAPPSale.forEach((k, v) -> {
            v.setRatio((double) v.getTotal() / eachAppTotal);
        });
    }

    /**
     * 计算各APP中的合计
     *
     * @param sortedAppMap 有序的APP数据
     * @return
     */
    private EachAPPSale getTotalAPPSale(TreeMap<String, EachAPPSale> sortedAppMap) {
        EachAPPSale totalAppSale = new EachAPPSale();
        totalAppSale.setProducts(new HashMap<String, Integer>());
        Set<String> productSet = new HashSet<>();
        //此处set是统计所有产品数，为了防止各APP并不是完全拥有不同价钱产品
        sortedAppMap.forEach((k1, v1) -> {
            v1.getProducts().forEach((k2, v2) -> {
                productSet.add(k2);
            });
        });
        //初始化合计里的产品
        productSet.forEach((p) -> {
            totalAppSale.getProducts().put(p, 0);
        });
        //累加合计列不同产品的数量，以及合计行的数量，
        sortedAppMap.forEach((k1, v1) -> {
            totalAppSale.setTotal(totalAppSale.getTotal() + v1.getTotal());
            v1.getProducts().forEach((k2, v2) -> {
                totalAppSale.getProducts().put(k2, totalAppSale.getProducts().get(k2) + v2);
            });
        });
        return totalAppSale;
    }

    /**
     * 处理源数据
     *
     * @param data           源数据
     * @param appRawDataList 各APP源数据
     * @return
     */
    private Map<String, EachAPPSale> dealAppRawData(List<?> data, List<AppRawData> appRawDataList) {
        Map<String, EachAPPSale> originAppMap = new HashMap<String, EachAPPSale>(data.size());
        appRawDataList.forEach(aData -> {
            if (originAppMap.containsKey(aData.getApp())) {
                EachAPPSale sale = originAppMap.get(aData.getApp());
                sale.getProducts().put(aData.getProduct(), aData.getCount());
            } else {
                EachAPPSale sale = new EachAPPSale();
                Map<String, Integer> product = new HashMap<String, Integer>();
                product.put(aData.getProduct(), aData.getCount());
                sale.setProducts(product);
                originAppMap.put(aData.getApp(), sale);
            }
        });
        return originAppMap;
    }

    /**
     * 计算各APP不同商品总和
     *
     * @param originAppMap 各APP源数据
     */
    private void calProductsTotal(Map<String, EachAPPSale> originAppMap) {
        originAppMap.forEach((k1, v1) -> {
            v1.getProducts().forEach((k2, v2) -> {
                v1.setTotal(v1.getTotal() + v2);
            });
        });
    }

}
