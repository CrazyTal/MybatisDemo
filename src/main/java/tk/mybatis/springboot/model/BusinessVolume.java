package tk.mybatis.springboot.model;

/**
 * Created by ltao on 2017/7/26.
 */
public class BusinessVolume {
    private String date;
    private int totalBusinessVolume;
    private int transactionFailure;
    private double transactionSuccessRate;//直接传给前端，后台不用
    private int systemFailure;
    private double systemSuccessRate;//直接传给前端，后台不用

    public BusinessVolume() {
    }

    public BusinessVolume(String date, int totalBusinessVolume, int transactionFailure, int systemFailure) {
        this.date = date;
        this.totalBusinessVolume = totalBusinessVolume;
        this.transactionFailure = transactionFailure;
        this.systemFailure = systemFailure;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public int getSystemFailure() {
        return systemFailure;
    }

    public void setSystemFailure(int systemFailure) {
        this.systemFailure = systemFailure;
    }

    public double getSystemSuccessRate() {
        return systemSuccessRate;
    }

    public void setSystemSuccessRate(double systemSuccessRate) {
        this.systemSuccessRate = systemSuccessRate;
    }
}
