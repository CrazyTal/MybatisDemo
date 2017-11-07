package tk.mybatis.springboot.model;

public class DataRecharge {

    private String date;
    private int totalBusinessVolume;
    private int transactionFailure;
    private double transactionSuccessRate;

    public DataRecharge() {
    }

    public DataRecharge(String date, int totalBusinessVolume, int transactionFailure) {
        this.date = date;
        this.totalBusinessVolume = totalBusinessVolume;
        this.transactionFailure = transactionFailure;
    }

    public DataRecharge(String date, int totalBusinessVolume, int transactionFailure, double transactionSuccessRate) {
        this.date = date;
        this.totalBusinessVolume = totalBusinessVolume;
        this.transactionFailure = transactionFailure;
        this.transactionSuccessRate = transactionSuccessRate;
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
}
