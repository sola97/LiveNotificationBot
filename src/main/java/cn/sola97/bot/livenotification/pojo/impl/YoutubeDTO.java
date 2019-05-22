package cn.sola97.bot.livenotification.pojo.impl;

import cn.sola97.bot.livenotification.enums.LiveEvent;
import cn.sola97.bot.livenotification.enums.LiveStatus;
import cn.sola97.bot.livenotification.pojo.LiveDTO;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class YoutubeDTO implements LiveDTO, Serializable {
    private LiveStatus liveStatus = LiveStatus.UNKNOWN;
    private LiveEvent liveEvent = LiveEvent.NONE;
    private PlayabilityStatus playabilityStatus;
    private VideoDetails videoDetails;
    private ChannelMetadataRenderer channelMetadataRenderer;
    private String profile;
    private String category;

    @NotNull
    @Override
    public LiveEvent getLiveEvent() {
        return liveEvent;
    }

    @NotNull
    public LiveStatus getLiveStatus() {
        return liveStatus;
    }

    @NotNull
    public String getTitle() {
        if (videoDetails != null)
            return videoDetails.getTitle();
        if (channelMetadataRenderer != null)
            return channelMetadataRenderer.getDescription().split("[\\p{P}\\s]+", 1)[0];
        return "null";
    }

    @Override
    public String getVideoUrl() {
        if (videoDetails != null)
            return "https://youtu.be/" + videoDetails.videoId;
        return null;
    }

    @Override
    public String getChannelUrl() {
        if (videoDetails != null)
            return "https://www.youtube.com/channel/" + videoDetails.channelId + "/live";
        if (channelMetadataRenderer != null)
            return channelMetadataRenderer.getChannelUrl();
        return null;
    }


    @Override
    public String getUserName() {
        if (videoDetails != null)
            return videoDetails.author;
        if (channelMetadataRenderer != null)
            return channelMetadataRenderer.title;
        return null;
    }

    public String getArea() {
        return category;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public String getThumbnail() {
        if (videoDetails != null)
            return videoDetails.thumbnail;
        return null;
    }


    public YoutubeDTO setLiveStatus(LiveStatus statusEnum) {
        this.liveStatus = statusEnum;
        return this;
    }

    @Override
    public YoutubeDTO setLiveEvent(LiveEvent liveEvent) {
        this.liveEvent = liveEvent;
        return this;
    }

    public void setPlayabilityStatus(PlayabilityStatus playabilityStatus) {
        this.playabilityStatus = playabilityStatus;
    }

    public void setChannelMetadataRenderer(ChannelMetadataRenderer channelMetadataRenderer) {
        this.channelMetadataRenderer = channelMetadataRenderer;
    }

    public void setVideoDetails(VideoDetails videoDetails) {
        this.videoDetails = videoDetails;
    }


    public class PlayabilityStatus implements Serializable {
        String status;
        String reason;

        public void setReason(String reason) {
            this.reason = reason;
            liveStatus = LiveStatus.CLOSED;
        }

        public void setStatus(String status) {
            this.status = status;
            switch (status) {
                case "LIVE_STREAM_OFFLINE":
                    liveStatus = LiveStatus.CLOSED;
                    break;
                case "OK":
                    if(reason==null)
                        liveStatus = LiveStatus.OPENED;
                    else
                        liveStatus = LiveStatus.CLOSED;
                    break;
                default:
                    liveStatus = LiveStatus.UNKNOWN;
                    break;
            }
        }


        @Override
        public String toString() {
            return "PlayabilityStatus{" +
                    "status='" + status + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "YoutubeDTO{" +
                "liveStatus=" + liveStatus +
                ", liveEvent=" + liveEvent +
                ", playabilityStatus=" + playabilityStatus +
                ", videoDetails=" + videoDetails +
                ", channelMetadataRenderer=" + channelMetadataRenderer +
                ", profile='" + profile + '\'' +
                ", category='" + category + '\'' +
                '}';
    }


    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}


class VideoDetails implements Serializable {
    String videoId;
    String title;
    String channelId;
    String viewCount;
    String author;
    String shortDescription;
    String thumbnail;

    public void setVideoId(String videoId) {
        this.videoId = videoId;
        this.thumbnail = String.format("https://i.ytimg.com/vi/%s/hqdefault.jpg", videoId);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getTitle() {
        return title == null ? "null" : title;
    }

    @Override
    public String toString() {
        return "VideoDetails{" +
                "videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", channelId='" + channelId + '\'' +
                ", viewCount='" + viewCount + '\'' +
                ", author='" + author + '\'' +
                ", shortDescription='" + shortDescription.substring(0, 20) + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}

class ChannelMetadataRenderer implements Serializable {
    String channelUrl;
    String description;
    String title;
    String vanityChannelUrl;

    public void setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVanityChannelUrl(String vanityChannelUrl) {
        this.vanityChannelUrl = vanityChannelUrl;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public String getDescription() {
        return description == null ? "null" : description;
    }

    public String getTitle() {
        return title;
    }

    public String getVanityChannelUrl() {
        return vanityChannelUrl;
    }

    @Override
    public String toString() {
        return "ChannelMetadataRenderer{" +
                "channelUrl='" + channelUrl + '\'' +
                ", description='" + description.substring(20) + '\'' +
                ", title='" + title + '\'' +
                ", vanityChannelUrl='" + vanityChannelUrl + '\'' +
                '}';
    }
}

