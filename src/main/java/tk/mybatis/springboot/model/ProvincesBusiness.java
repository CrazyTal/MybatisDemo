package tk.mybatis.springboot.model;

public class ProvincesBusiness {

    private String province;
    private int totalBusinessVolume;
    private int transactionFailure;
    private double transactionSuccessRate;
    private double ratio;

    public ProvincesBusiness() {
    }

    public ProvincesBusiness(String province, int totalBusinessVolume, int transactionFailure, double transactionSuccessRate, double ratio) {
        this.province = province;
        this.totalBusinessVolume = totalBusinessVolume;
        this.transactionFailure = transactionFailure;
        this.transactionSuccessRate = transactionSuccessRate;
        this.ratio = ratio;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getTotalBusinessVolume() {
        return totalBusinessVolume;
    }

    public void setTotalBusinessVolume(int totalBusinessVolume) {
        this.totalBusinessVolume = totalBusinessVolume;
    }

    public int getTransactionFailure() {
        return transactionFailure;
    }

    public void setTransactionFailure(int transactionFailure) {
        this.transactionFailure = transactionFailure;
    }

    public double getTransactionSuccessRate() {
        return transactionSuccessRate;
    }

    public void setTransactionSuccessRate(double transactionSuccessRate) {
        this.transactionSuccessRate = transactionSuccessRate;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
