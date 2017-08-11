package tk.mybatis.springboot;

import org.junit.Test;
import tk.mybatis.springboot.model.EachProductBusiness;
import tk.mybatis.springboot.model.InspectionDaily;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ltao on 2017/8/10.
 */
public class ListTest {

    @Test
    public void testSortList() {
        InspectionDaily inspectionDaily = new InspectionDaily();
        inspectionDaily.setEachProductBusinessVolume(new ArrayList<>(8));
        for (int i = 0; i < 8; i++) {
            inspectionDaily.getEachProductBusinessVolume().add(
                    new EachProductBusiness("产品" + i, (int) (Math.random() * 10)));
        }
        System.out.println("first");
        System.out.println(inspectionDaily.getEachProductBusinessVolume());
        System.out.println("second");
        inspectionDaily.getEachProductBusinessVolume().sort((a1, a2) -> {
            return a1.getBusinessSuccess() - a2.getBusinessSuccess();
        });
        System.out.println(inspectionDaily.getEachProductBusinessVolume());


        List list = new ArrayList(5);
        list.add(1);
        list.add(2);
        list.add(3);
//        Collections.sort(list);
    }
}
