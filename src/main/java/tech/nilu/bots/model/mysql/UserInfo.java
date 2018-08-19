package tech.nilu.bots.model.mysql;

import tech.nilu.bots.dto.BotType;

import javax.persistence.*;
import java.util.Date;

@Entity
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    private BotType botType;

    private String userId;

    private String userName;

    private String niluId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastBountyDate;

    @ManyToOne
    private Bounty lastBounty;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNiluId() {
        return niluId;
    }

    public void setNiluId(String niluId) {
        this.niluId = niluId;
    }


    public Bounty getLastBounty() {
        return lastBounty;
    }

    public void setLastBounty(Bounty lastBounty) {
        this.lastBounty = lastBounty;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastBountyDate() {
        return lastBountyDate;
    }

    public void setLastBountyDate(Date lastBountyDate) {
        this.lastBountyDate = lastBountyDate;
    }
}
