package tech.nilu.bots.proxy.bots.instagram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetMediaCommentsRequest;
import org.brunocvcunha.instagram4j.requests.InstagramTagFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.nilu.bots.ActionCodes;
import tech.nilu.bots.dto.CommentInfo;
import tech.nilu.bots.dto.Comments;
import tech.nilu.bots.exception.ApplicationException;
import tech.nilu.bots.proxy.bots.instagram.dto.InstagramLikersRequest;
import tech.nilu.bots.proxy.bots.instagram.dto.InstagramLikersResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class InstagramBot {


    Instagram4j instagram;
    static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

    public InstagramBot(@Autowired Instagram4j instagram) {
        this.instagram = instagram;
    }

    public Comments getComments(String post, String lastCursor, int count) {
        try {
            Comments ret = new Comments();
            instagram.login();
            ObjectMapper om = new ObjectMapper();
            List<CommentInfo> comments = new ArrayList<>();
            String maxId = lastCursor;
            while (comments.size() < count) {
                InstagramGetMediaCommentsResult commentsResult = instagram
                        .sendRequest(new InstagramGetMediaCommentsRequest(convertUrlToMedia(post)
                                + "", maxId));
                if (commentsResult.getNext_max_id() != null) {
                    CommentCursor cs = om.readValue(commentsResult.getNext_max_id(), CommentCursor.class);
                    maxId = cs.serverCursor;
                } else
                    maxId = null;
                ret.setTotal(commentsResult.getComment_count());
                ret.setLastIndex(maxId);
                if ( commentsResult.getComments() != null ) {
                    for (InstagramComment c : commentsResult.getComments()) {
                        if (comments.size() < count) {
                            comments.add(new CommentInfo(c.getText()
                                    , c.getUser_id() + ""
                                    , new Date(c.getCreated_at_utc())
                                    , c.getUser().username
                                    , null));
                        }
                    }
                }
                if (maxId == null || comments.size() >= count || comments.size() >= commentsResult.getComment_count()) {
                    break;
                }
            }
            ret.setComments(comments);
            return ret;
        } catch (Exception e) {
            throw new ApplicationException(ActionCodes.TYPE_GENERAL_ERROR, ActionCodes.INSTAGRAM_BOT_ERROR, e);
        }
    }

    public List<String> getTagFeeds(String tag) throws Exception {
        instagram.login();
        List<String> tags = new ArrayList<>();
        InstagramFeedResult tagFeed = instagram.sendRequest(new InstagramTagFeedRequest(tag));
        for (InstagramFeedItem feedResult : tagFeed.getItems()) {
            tags.add(feedResult.getId());
        }
        return tags;
    }

    public List<String> getLikes(String post) throws Exception {
        instagram.login();
        List<String> likes = new ArrayList<>();
        InstagramLikersResult likers = instagram.sendRequest(new InstagramLikersRequest(convertUrlToMedia(post)));
        for (InstagramUser liker : likers.getUsers()) {
            likes.add(liker.getUsername());
        }
        return likes;
    }

    public static String convertMediaIdToUrl(long mediaId) {

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
        String shortened_id = "";

        while (mediaId > 0) {
            int remainder = (int) (mediaId % 64);
            mediaId = (mediaId / 64);
            shortened_id = alphabet.charAt(remainder) + shortened_id;
        }

        return "https://instagram.com/p/" + shortened_id + "/";

    }

    public static long convertUrlToMedia(String url) {
        String post = url;
        int idx = url.lastIndexOf("/");
        if (idx != -1)
            post = url.substring(idx + 1);
        idx = url.lastIndexOf("?");
        if (idx != -1)
            post = url.substring(0, idx);
        StringBuilder u = new StringBuilder(post);
        long ret = 0;
        while (u.length() > 0) {
            char ch = u.charAt(0);
            for (int i = 0; i < alphabet.toCharArray().length; i++) {
                if (ch == alphabet.toCharArray()[i]) {
                    ret = ret * 64 + i;
                    break;
                }
            }
            u = u.delete(0, 1);
        }
        return ret;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommentCursor {
        @JsonProperty("server_cursor")
        private String serverCursor;

        public String getServerCursor() {
            return serverCursor;
        }

        public void setServerCursor(String serverCursor) {
            this.serverCursor = serverCursor;
        }
    }
}
