package tk.mybatis.springboot.model;

public class AppRawData {

    private String app;
    private String product;
    private int count;

    public AppRawData() {
    }

    public AppRawData(String app, String product, int count) {
        this.app = app;
        this.product = product;
        this.count = count;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
