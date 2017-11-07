package tk.mybatis.springboot;


import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;
import tk.mybatis.springboot.model.EachAPPSale;

import java.io.Writer;
import java.util.*;

/**
 * Created by ltao on 2017/8/7.
 */
public class MapTest {

    private String name;
    private Map<String, Integer> map;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }

    @Test
    public void test1() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("B", 97);
        map.put("D", 96);
        map.put("C", 94);
        map.put("A", 99);

        Iterator<String> i1 = map.keySet().iterator();
        int index = 0;
        System.out.println(i1.next());
        while (i1.hasNext()) {
            System.out.println(i1.next());
            index++;
            if (index > 1) {
                break;
            }
        }
        Iterator<String> i2 = map.keySet().iterator();
        index = 0;
        while (i2.hasNext()) {
            System.out.println(i2.next());
            System.out.println(i2.next());
            index++;
            if (index > 1) {
                break;
            }
        }


//        MapTest mapTest = new MapTest();
//        mapTest.setName("aa");
//        mapTest.setMap(map);
//        System.out.println(JSONObject.fromObject(mapTest));
//        Map<String, MapTest> mapTestMap = new HashMap<>();
//        mapTestMap.put("第一个", mapTest);
//        mapTestMap.put("第三个", mapTest);
//        mapTestMap.put("第二个", mapTest);
//        System.out.println(JSONArray.fromObject(mapTestMap));
//        System.out.println(JSONObject.fromObject(mapTestMap));
    }

    @Test
    public void test4(){
        JSONObject o = JSONObject.fromObject("{a:b}");
    }

    @Test
    public void test2() {
        Map<Integer, Integer> treeMap = new TreeMap<>();
        treeMap.put(24, 456);
        treeMap.put(9, 767);
        treeMap.put(18, 45);
        for (Integer i : treeMap.keySet()) {
            System.out.println(i);
        }
        System.out.println(treeMap);
    }

    @Test
    public void test3() {
        EachAPPSale eachAPPSale = new EachAPPSale();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("9元", 1);
        linkedHashMap.put("24元", 2);
        eachAPPSale.setProducts(linkedHashMap);
        Map<String, EachAPPSale> eachAPPSaleMap = new TreeMap<>();
        eachAPPSaleMap.put("腾讯", eachAPPSale);
        eachAPPSaleMap.forEach((k1, v1) -> {
            v1.getProducts().forEach((k2, v2) -> {
                v1.setTotal(v1.getTotal() + v2);
            });
        });
        System.out.println(JSONObject.fromObject(eachAPPSaleMap));
    }

    public static void main(String[] args) {
        Map<String, Double> map = new HashMap<String, Double>();
        ValueComparator bvc = new ValueComparator(map);
        TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);

        map.put("A", 69.5);
        map.put("B", 87.4);
        map.put("C", 77.4);
        map.put("D", 67.3);

        System.out.println("unsorted map: " + map);
        sorted_map.putAll(map);
        System.out.println("results: " + JSONObject.fromObject(sorted_map));
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.putAll(sorted_map);
        linkedHashMap.put("sdas", 13.1);
        System.out.println(sorted_map);
        System.out.println(linkedHashMap);
        Set<String> set = new HashSet<>();
        set = sorted_map.keySet();
        System.out.println(set);
        for (String a : set) {
            System.out.println(a);
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}

class ValueComparator implements Comparator<String> {

    Map<String, Double> base;

    public ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
