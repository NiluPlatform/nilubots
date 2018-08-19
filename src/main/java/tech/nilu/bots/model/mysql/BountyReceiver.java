package tech.nilu.bots.model.mysql;

import tech.nilu.bots.convertor.MysqlBigIntegerConverter;
import tech.nilu.bots.dto.BountyReceiverStatus;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
public class BountyReceiver {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Bounty bounty;

    @ManyToOne
    private UserInfo user;

    private String niluId;

    @Convert(converter = MysqlBigIntegerConverter.class)
    private BigInteger value;

    @Enumerated(EnumType.STRING)
    private BountyReceiverStatus status;

    private Date createDate;

    private Date lastUpdateDate;

    private int executeCount;

    private String error;

    private String txHash;

    private boolean refund;

    public BountyReceiver() {
    }

    public BountyReceiver(String niluId, BigInteger value) {
        this.niluId = niluId;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Bounty getBounty() {
        return bounty;
    }

    public void setBounty(Bounty bounty) {
        this.bounty = bounty;
    }

    public BountyReceiverStatus getStatus() {
        return status;
    }

    public void setStatus(BountyReceiverStatus status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public int getExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(int executeCount) {
        this.executeCount = executeCount;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    public String getNiluId() {
        return niluId;
    }

    public void setNiluId(String niluId) {
        this.niluId = niluId;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public boolean isRefund() {
        return refund;
    }

    public void setRefund(boolean refund) {
        this.refund = refund;
    }
}
