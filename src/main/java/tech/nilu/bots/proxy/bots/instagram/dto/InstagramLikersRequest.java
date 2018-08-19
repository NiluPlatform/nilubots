package tech.nilu.bots.proxy.bots.instagram.dto;

import org.brunocvcunha.instagram4j.requests.InstagramGetRequest;

public class InstagramLikersRequest extends InstagramGetRequest<InstagramLikersResult> {

    private final String media;

    public String getUrl() {
        String url = "media/" + this.media + "/likers/";

        return url;
    }

    public InstagramLikersResult parseResult(int statusCode, String content) {
        try {
            return (InstagramLikersResult)this.parseJson(statusCode, content, InstagramLikersResult.class);
        } catch (Throwable var4) {
            throw var4;
        }
    }

    public InstagramLikersRequest(Long media) {
        if ( media == null) {
            throw new NullPointerException("media");
        } else {
            this.media = media + "";
        }
    }


}
