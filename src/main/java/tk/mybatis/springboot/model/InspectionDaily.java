package tk.mybatis.springboot.model;

import java.util.List;
import java.util.Map;

public class InspectionDaily {

    private List<BusinessVolume> totalBusinessVolume;
    private List<DataRecharge> dataRecharge;
    private List<EachProductBusiness> eachProductBusinessVolume;
    private List<EachChannelBusiness> eachChannelBusinessVolume;
    private List<ProvincesBusiness> provincesBusinesses;
    private List<ProvincesDoD> provincesDoD;
    private List<TransactionAmount> transactionAmount;
    private Map<String, EachAPPSale> eachAPPSale;

    public List<BusinessVolume> getTotalBusinessVolume() {
        return totalBusinessVolume;
    }

    public void setTotalBusinessVolume(List<BusinessVolume> totalBusinessVolume) {
        this.totalBusinessVolume = totalBusinessVolume;
    }

    public List<DataRecharge> getDataRecharge() {
        return dataRecharge;
    }

    public void setDataRecharge(List<DataRecharge> dataRecharge) {
        this.dataRecharge = dataRecharge;
    }

    public List<EachProductBusiness> getEachProductBusinessVolume() {
        return eachProductBusinessVolume;
    }

    public void setEachProductBusinessVolume(List<EachProductBusiness> eachProductBusinessVolume) {
        this.eachProductBusinessVolume = eachProductBusinessVolume;
    }

    public List<EachChannelBusiness> getEachChannelBusinessVolume() {
        return eachChannelBusinessVolume;
    }

    public void setEachChannelBusinessVolume(List<EachChannelBusiness> eachChannelBusinessVolume) {
        this.eachChannelBusinessVolume = eachChannelBusinessVolume;
    }

    public List<ProvincesBusiness> getProvincesBusinesses() {
        return provincesBusinesses;
    }

    public void setProvincesBusinesses(List<ProvincesBusiness> provincesBusinesses) {
        this.provincesBusinesses = provincesBusinesses;
    }

    public List<ProvincesDoD> getProvincesDoD() {
        return provincesDoD;
    }

    public void setProvincesDoD(List<ProvincesDoD> provincesDoD) {
        this.provincesDoD = provincesDoD;
    }

    public List<TransactionAmount> getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(List<TransactionAmount> transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Map<String, EachAPPSale> getEachAPPSale() {
        return eachAPPSale;
    }

    public void setEachAPPSale(Map<String, EachAPPSale> eachAPPSale) {
        this.eachAPPSale = eachAPPSale;
    }
}
