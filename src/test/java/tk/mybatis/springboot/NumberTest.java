package tk.mybatis.springboot;

import org.junit.Test;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ltao on 2017/7/31.
 */
public class NumberTest {
    @Test
    public void testDeci() {
        double num = 0.21535;
        DecimalFormat df2 = new DecimalFormat("0.00%");
        System.out.println(df2.format(num));
    }

    @Test
    public void testLinkedHashMap() {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < 1000000; i++) {
            map.put("" + i, i);
        }

        long time1 = System.currentTimeMillis();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {

        }
        long time2 = System.currentTimeMillis();
        map.forEach((k, v) -> {});
        long time3 = System.currentTimeMillis();
        System.out.println("第一种: " + (time2 - time1));
        System.out.println("第二种: " + (time3 - time2));

    }

}
