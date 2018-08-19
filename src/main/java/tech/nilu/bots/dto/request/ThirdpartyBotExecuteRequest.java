package tech.nilu.bots.dto.request;

import tech.nilu.bots.model.mysql.BountyReceiver;

import java.util.ArrayList;
import java.util.List;

public class ThirdpartyBotExecuteRequest extends BaseRequest {

    private String bountyNiluAddress;

    private List<BountyReceiver> receivers = new ArrayList<>();

    public String getBountyNiluAddress() {
        return bountyNiluAddress;
    }

    public void setBountyNiluAddress(String bountyNiluAddress) {
        this.bountyNiluAddress = bountyNiluAddress;
    }

    public List<BountyReceiver> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<BountyReceiver> receivers) {
        this.receivers = receivers;
    }
}
