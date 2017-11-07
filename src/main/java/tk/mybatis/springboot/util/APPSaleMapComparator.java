package tk.mybatis.springboot.util;

import tk.mybatis.springboot.model.EachAPPSale;

import java.util.Comparator;
import java.util.Map;

public class APPSaleMapComparator implements Comparator<String> {
    Map<String, EachAPPSale> map;

    public APPSaleMapComparator(Map<String, EachAPPSale> map) {
        this.map = map;
    }

    public int compare(String m1, String m2) {
        if (map.get(m1).getTotal() >= map.get(m2).getTotal()) {
            return -1;
        }
        return 1;
    }

}

