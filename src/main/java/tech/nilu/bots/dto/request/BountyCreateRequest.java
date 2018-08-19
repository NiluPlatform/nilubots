package tech.nilu.bots.dto.request;

import tech.nilu.bots.dto.ActionType;
import tech.nilu.bots.dto.BotType;

import java.math.BigInteger;
import java.util.Date;

public class BountyCreateRequest {

    private BotType botType;

    private ActionType actionType;

    private Date checkDate;

    private int winnersCount;

    private String postAddress;

    private BigInteger bountyAmount;

    private String tokenAddress;

    private String refundAddress;

    public BountyCreateRequest() {
    }

    public BountyCreateRequest(BotType botType, ActionType actionType, Date checkDate, int winnersCount, String postAddress, BigInteger bountyAmount, String tokenAddress, String refundAddress) {
        this.botType = botType;
        this.actionType = actionType;
        this.checkDate = checkDate;
        this.winnersCount = winnersCount;
        this.postAddress = postAddress;
        this.bountyAmount = bountyAmount;
        this.tokenAddress = tokenAddress;
        this.refundAddress = refundAddress;
    }

    public BotType getBotType() {
        return botType;
    }

    public void setBotType(BotType botType) {
        this.botType = botType;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public int getWinnersCount() {
        return winnersCount;
    }

    public void setWinnersCount(int winnersCount) {
        this.winnersCount = winnersCount;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public BigInteger getBountyAmount() {
        return bountyAmount;
    }

    public void setBountyAmount(BigInteger bountyAmount) {
        this.bountyAmount = bountyAmount;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getRefundAddress() {
        return refundAddress;
    }

    public void setRefundAddress(String refundAddress) {
        this.refundAddress = refundAddress;
    }

}
