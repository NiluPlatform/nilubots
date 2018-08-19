package tech.nilu.bots.model.mysql;

import tech.nilu.bots.convertor.MysqlBigIntegerConverter;
import tech.nilu.bots.dto.ActionType;
import tech.nilu.bots.dto.BotType;
import tech.nilu.bots.dto.BountyStatus;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
public class Bounty {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    private BotType botType;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private Date registerDate;

    private Date checkDate;

    private int winnersCount;

    private String postAddress;

    @Convert(converter = MysqlBigIntegerConverter.class)
    private BigInteger bountyAmount = BigInteger.ZERO;

    @Convert(converter = MysqlBigIntegerConverter.class)
    private BigInteger totalAmount = BigInteger.ZERO;

    @Convert(converter = MysqlBigIntegerConverter.class)
    private BigInteger spentAmount = BigInteger.ZERO;

    @Convert(converter = MysqlBigIntegerConverter.class)
    private BigInteger refundAmount = BigInteger.ZERO;

    private double feeInNilu;

    private String tokenAddress;

    private String refundAddress;

    private String bountyNiluAddress;

    private String bountyMnemonic;

    private int maxRetryCount = 3 ;

    private int retryCount;

    private int failedCount;

    private int successCount;

    private int finalWinnerCount;

    private String lastError;

    private Date lastCheckDate;

    @Enumerated(EnumType.STRING)
    private BountyStatus status;

    private String botMetaData;

    private String refundTxHash;

    private boolean refundRequired;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
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

    public BigInteger getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigInteger totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigInteger getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigInteger spentAmount) {
        this.spentAmount = spentAmount;
    }

    public BigInteger getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigInteger refundAmount) {
        this.refundAmount = refundAmount;
    }

    public double getFeeInNilu() {
        return feeInNilu;
    }

    public void setFeeInNilu(double feeInNilu) {
        this.feeInNilu = feeInNilu;
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

    public String getBountyNiluAddress() {
        return bountyNiluAddress;
    }

    public void setBountyNiluAddress(String bountyNiluAddress) {
        this.bountyNiluAddress = bountyNiluAddress;
    }

    public String getBountyMnemonic() {
        return bountyMnemonic;
    }

    public void setBountyMnemonic(String bountyMnemonic) {
        this.bountyMnemonic = bountyMnemonic;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public Date getLastCheckDate() {
        return lastCheckDate;
    }

    public void setLastCheckDate(Date lastCheckDate) {
        this.lastCheckDate = lastCheckDate;
    }

    public BountyStatus getStatus() {
        return status;
    }

    public void setStatus(BountyStatus status) {
        this.status = status;
    }

    public String getBotMetaData() {
        return botMetaData;
    }

    public void setBotMetaData(String botMetaData) {
        this.botMetaData = botMetaData;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFinalWinnerCount() {
        return finalWinnerCount;
    }

    public void setFinalWinnerCount(int finalWinnerCount) {
        this.finalWinnerCount = finalWinnerCount;
    }

    public String getRefundTxHash() {
        return refundTxHash;
    }

    public void setRefundTxHash(String refundTxHash) {
        this.refundTxHash = refundTxHash;
    }

    public boolean isRefundRequired() {
        return refundRequired;
    }

    public void setRefundRequired(boolean refundRequired) {
        this.refundRequired = refundRequired;
    }
}
