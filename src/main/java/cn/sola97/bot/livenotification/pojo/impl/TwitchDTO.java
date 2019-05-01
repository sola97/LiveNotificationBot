package cn.sola97.bot.livenotification.pojo.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import cn.sola97.bot.livenotification.enums.LiveEvent;
import cn.sola97.bot.livenotification.enums.LiveStatus;
import cn.sola97.bot.livenotification.pojo.LiveDTO;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class TwitchDTO implements LiveDTO, Serializable {
    private String game;
    private String viewers;
    private String stream_type;
    private String created_at;
    private Preview preview;
    private Channel channel;
    private LiveStatus liveStatus = LiveStatus.UNKNOWN;
    private LiveEvent liveEvent = LiveEvent.NONE;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TwitchDTO that = (TwitchDTO) o;
        return Objects.equals(game, that.game) &&
                Objects.equals(viewers, that.viewers) &&
                Objects.equals(stream_type, that.stream_type) &&
                Objects.equals(created_at, that.created_at) &&
                Objects.equals(preview, that.preview) &&
                Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game, viewers, stream_type, created_at, preview, channel);
    }

    @Override
    public String toString() {
        return "TwitchDTO{" +
                "game='" + game + '\'' +
                ", viewers='" + viewers + '\'' +
                ", stream_type='" + stream_type + '\'' +
                ", created_at='" + created_at + '\'' +
                ", preview=" + preview +
                ", channel=" + channel +
                ", liveStatus=" + liveStatus +
                ", liveEvent=" + liveEvent +
                '}';
    }

    @NotNull
    @Override
    public String getTitle() {
        return Optional.ofNullable(channel).map(Channel::getTitle).orElse("null");
    }

    @Override
    public String getVideoUrl() {
        return Optional.ofNullable(channel).map(Channel::getUrl).orElse(null);
    }

    @Override
    public String getChannelUrl() {
        return Optional.ofNullable(channel).map(Channel::getUrl).orElse(null);
    }

    @Override
    public String getUserName() {
        return Optional.ofNullable(channel).map(Channel::getDisplay_name).orElse(null);
    }

    @Override
    public String getArea() {
        return Optional.ofNullable(channel).map(Channel::getGame).orElse(null);
    }

    @Override
    public String getProfile() {
        return Optional.ofNullable(channel).map(Channel::getLogo).orElse(null);
    }

    @Override
    public String getThumbnail() {
        if (liveStatus == LiveStatus.OPENED) {
            return preview.getMedium();
        } else {
            String v = Optional.ofNullable(channel).map(Channel::getVideo_banner).orElse(null);
            String p = Optional.ofNullable(channel).map(Channel::getProfile_banner).orElse(null);
            return v == null ? p : v;
        }
    }

    @NotNull
    @Override
    public LiveStatus getLiveStatus() {
        return liveStatus;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public void setViewers(String viewers) {
        this.viewers = viewers;
    }

    public void setStream_type(String stream_type) {
        this.stream_type = stream_type;
        if (stream_type.equals("live"))
            this.liveStatus = LiveStatus.OPENED;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setPreview(Preview preview) {
        this.preview = preview;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public TwitchDTO setLiveStatus(LiveStatus liveStatus) {
        this.liveStatus = liveStatus;
        return this;
    }

    @NotNull
    @Override
    public LiveEvent getLiveEvent() {
        return liveEvent;
    }

    @Override
    public TwitchDTO setLiveEvent(LiveEvent event) {
        this.liveEvent = event;
        return this;
    }
}


class Channel implements Serializable {
    private String display_name;
    @JsonProperty("status")
    private String title;
    private String logo;
    private String video_banner;
    private String profile_banner;
    private String url;
    private String game;
    private String name;

    public String getProfile_banner() {
        return profile_banner;
    }

    public void setProfile_banner(String profile_banner) {
        this.profile_banner = profile_banner;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getTitle() {
        return title == null ? "null" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getVideo_banner() {
        return video_banner;
    }

    public void setVideo_banner(String video_banner) {
        this.video_banner = video_banner;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(display_name, channel.display_name) &&
                Objects.equals(title, channel.title) &&
                Objects.equals(logo, channel.logo) &&
                Objects.equals(video_banner, channel.video_banner) &&
                Objects.equals(url, channel.url) &&
                Objects.equals(game, channel.game) &&
                Objects.equals(name, channel.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(display_name, title, logo, video_banner, url, game, name);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "display_name='" + display_name + '\'' +
                ", title='" + title + '\'' +
                ", logo='" + logo + '\'' +
                ", video_banner='" + video_banner + '\'' +
                ", url='" + url + '\'' +
                ", game='" + game + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

class Preview implements Serializable {
    private String small;
    private String medium;
    private String large;

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Preview preview = (Preview) o;
        return Objects.equals(small, preview.small) &&
                Objects.equals(medium, preview.medium) &&
                Objects.equals(large, preview.large);
    }

    @Override
    public int hashCode() {
        return Objects.hash(small, medium, large);
    }

    @Override
    public String toString() {
        return "Preview{" +
                "small='" + small + '\'' +
                ", medium='" + medium + '\'' +
                ", large='" + large + '\'' +
                '}';
    }
}