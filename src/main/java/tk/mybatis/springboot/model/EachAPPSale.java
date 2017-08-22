package tk.mybatis.springboot.model;

import java.util.LinkedHashMap;

/**
 * Created by ltao on 2017/7/31.
 */
public class EachAPPSale {
    // TODO: 2017/8/22
    /**
     * 将接收数据时产品设置为TreeMap<Integer, Integer>
     * 将写入excel以及发送前端数据设为LinkedHashMao<String, Integer>
     * 因为InspectionDaily中含有总量以及比例，此数据在库中没有（也可在存储过程中计算）
     * 但EachAPPSale涉及两方面（横向和纵向）排序
     * todo 查看多余字节的对象是否能接收数据
     */

    private LinkedHashMap<String, Integer> products;
    private int total;
    private int ratio;

    public EachAPPSale() {
    }

    public LinkedHashMap<String, Integer> getProducts() {
        return products;
    }

    public void setProducts(LinkedHashMap<String, Integer> products) {
        this.products = products;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }
}
