package tk.mybatis.springboot.model;

public class EachProductBusiness {

    private String business;
    private int businessSuccess;
    private double ratio;

    public EachProductBusiness() {
    }

    public EachProductBusiness(String business, int businessSuccess) {
        this.business = business;
        this.businessSuccess = businessSuccess;
    }

    public EachProductBusiness(String business, int businessSuccess, double ratio) {
        this.business = business;
        this.businessSuccess = businessSuccess;
        this.ratio = ratio;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public int getBusinessSuccess() {
        return businessSuccess;
    }

    public void setBusinessSuccess(int businessSuccess) {
        this.businessSuccess = businessSuccess;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

}
