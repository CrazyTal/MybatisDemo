package tk.mybatis.springboot.model;

import java.util.Map;

public class EachAPPSale {

    private Map<String, Integer> products;
    private int total;
    private double ratio;

    public EachAPPSale() {
    }

    public Map<String, Integer> getProducts() {
        return products;
    }

    public void setProducts(Map<String, Integer> products) {
        this.products = products;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
