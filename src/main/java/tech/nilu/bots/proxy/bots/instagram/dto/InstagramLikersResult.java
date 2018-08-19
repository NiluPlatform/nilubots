//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package tech.nilu.bots.proxy.bots.instagram.dto;

import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.brunocvcunha.instagram4j.requests.payload.StatusResult;

import java.util.List;

public class InstagramLikersResult extends StatusResult {

    private int user_count;
    private List<InstagramUser> users;

    public int getUser_count() {
        return user_count;
    }

    public void setUser_count(int user_count) {
        this.user_count = user_count;
    }

    public List<InstagramUser> getUsers() {
        return users;
    }

    public void setUsers(List<InstagramUser> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InstagramLikesResult{");
        sb.append("user_count=").append(user_count);
        sb.append(", users=").append(users);
        sb.append('}');
        return sb.toString();
    }
}
