package tk.mybatis.springboot.model;

/**
 * Created by ltao on 2017/7/31.
 */
public class ProvincesDoD {

    private String province;
    private int businessVolume_T_1;
    private int businessVolume_T;

    public ProvincesDoD() {
    }

    public ProvincesDoD(String province, int businessVolume_T_1, int businessVolume_T) {
        this.province = province;
        this.businessVolume_T_1 = businessVolume_T_1;
        this.businessVolume_T = businessVolume_T;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getBusinessVolume_T_1() {
        return businessVolume_T_1;
    }

    public void setBusinessVolume_T_1(int businessVolume_T_1) {
        this.businessVolume_T_1 = businessVolume_T_1;
    }

    public int getBusinessVolume_T() {
        return businessVolume_T;
    }

    public void setBusinessVolume_T(int businessVolume_T) {
        this.businessVolume_T = businessVolume_T;
    }
}
