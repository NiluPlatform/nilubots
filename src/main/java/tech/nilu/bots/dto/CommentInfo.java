package tech.nilu.bots.dto;

import java.util.Date;

public class CommentInfo {
    private String userId;
    private String username;
    private String niluId;
    private Date date;
    private String text;

    public CommentInfo(String text, String userId, Date date, String username, String niluId) {
        this.userId = userId;
        this.username = username;
        this.niluId = niluId;
        this.date = date;
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNiluId() {
        return niluId;
    }

    public void setNiluId(String niluId) {
        this.niluId = niluId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommentInfo{");
        sb.append("userId='").append(userId).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", niluId='").append(niluId).append('\'');
        sb.append(", date=").append(date);
        sb.append(", text='").append(text).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
