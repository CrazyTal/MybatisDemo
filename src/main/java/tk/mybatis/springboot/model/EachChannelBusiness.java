package tk.mybatis.springboot.model;

public class EachChannelBusiness {

    private String channel;
    private int businessSuccess;
    private double ratio;

    public EachChannelBusiness() {
    }

    public EachChannelBusiness(String channel, int businessSuccess) {
        this.channel = channel;
        this.businessSuccess = businessSuccess;
    }

    public EachChannelBusiness(String channel, int businessSuccess, double ratio) {
        this.channel = channel;
        this.businessSuccess = businessSuccess;
        this.ratio = ratio;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
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
