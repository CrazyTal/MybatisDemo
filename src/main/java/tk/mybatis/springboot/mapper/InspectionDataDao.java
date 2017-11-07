package tk.mybatis.springboot.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 巡检日报数据dao层
 */
@Repository
public interface InspectionDataDao {

    //查询总业务量巡检数据
    List<List<?>> queryTotalDailyData(String queryDate);
    //InspectionDaily queryTotalDailyData(String queryDate);

    //查询卓望巡检数据
    List<List<?>> queryZwDailyData(String queryDate);

    //查询任我购巡检数据
    List<List<?>> queryRwgDailyData(String queryDate);

    //查询任我看巡检数据
    List<List<?>> queryRwkDailyData(String queryDate);

}
