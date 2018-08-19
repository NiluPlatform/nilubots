package tech.nilu.bots.dto.response;

public class BountyCreateResponse extends BaseResponse {

    private double feeInNilu;

    private String bountyNiluAddress;

    public double getFeeInNilu() {
        return feeInNilu;
    }

    public void setFeeInNilu(double feeInNilu) {
        this.feeInNilu = feeInNilu;
    }

    public String getBountyNiluAddress() {
        return bountyNiluAddress;
    }

    public void setBountyNiluAddress(String bountyNiluAddress) {
        this.bountyNiluAddress = bountyNiluAddress;
    }
}
