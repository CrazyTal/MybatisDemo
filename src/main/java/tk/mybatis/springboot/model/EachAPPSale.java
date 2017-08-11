package tk.mybatis.springboot.model;

/**
 * Created by ltao on 2017/7/31.
 */
public class EachAPPSale {

    private int price_9;
    private int price_24;
    private int total;
    private int ratio;

    public EachAPPSale() {
    }

    public EachAPPSale(int price_9, int price_24, int total, int ratio) {
        this.price_9 = price_9;
        this.price_24 = price_24;
        this.total = total;
        this.ratio = ratio;
    }

    public int getPrice_9() {
        return price_9;
    }

    public void setPrice_9(int price_9) {
        this.price_9 = price_9;
    }

    public int getPrice_24() {
        return price_24;
    }

    public void setPrice_24(int price_24) {
        this.price_24 = price_24;
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
