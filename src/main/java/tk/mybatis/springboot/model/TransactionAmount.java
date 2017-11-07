package tk.mybatis.springboot.model;

public class TransactionAmount {

    private String product;
    private double price;
    private int singleDayAmount;
    private double transactionAmount;

    public TransactionAmount() {
    }

    public TransactionAmount(String product, double price, int singleDayAmount, double transactionAmount) {
        this.product = product;
        this.price = price;
        this.singleDayAmount = singleDayAmount;
        this.transactionAmount = transactionAmount;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSingleDayAmount() {
        return singleDayAmount;
    }

    public void setSingleDayAmount(int singleDayAmount) {
        this.singleDayAmount = singleDayAmount;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}
