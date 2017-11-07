package tk.mybatis.springboot.model;

public class ProvincesDoD {

    private String province;
    private int businessVolumeT_1;
    private int businessVolumeT;

    public ProvincesDoD() {
    }

    public ProvincesDoD(String province, int businessVolumeT_1, int businessVolumeT) {
        this.province = province;
        this.businessVolumeT_1 = businessVolumeT_1;
        this.businessVolumeT = businessVolumeT;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getBusinessVolumeT_1() {
        return businessVolumeT_1;
    }

    public void setBusinessVolumeT_1(int businessVolumeT_1) {
        this.businessVolumeT_1 = businessVolumeT_1;
    }

    public int getBusinessVolumeT() {
        return businessVolumeT;
    }

    public void setBusinessVolumeT(int businessVolumeT) {
        this.businessVolumeT = businessVolumeT;
    }
}
