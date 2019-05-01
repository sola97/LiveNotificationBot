package cn.sola97.bot.livenotification.observer;

import cn.sola97.bot.livenotification.enums.Colors;
import cn.sola97.bot.livenotification.enums.Icons;


public class YoutubeObserver extends BaseObserver {

    private String channelId;

    public YoutubeObserver(String discordChannelId, String liveChannelId) {
        super(discordChannelId);
        this.channelId = liveChannelId;
    }

    @Override
    public int getEmbedCorlor() {
        return Colors.YOUTUBE_RED.value;
    }

    @Override
    public String toString() {
        return "YoutubeObserver{" +
                "channelId='" + channelId + '\'' +
                ", enabled=" + enabled +
                ", liveDTO=" + liveDTO +
                ", discordChannelId='" + discordChannelId + '\'' +
                ", mentions=" + mentions +
                ", messageMask=" + toBinaryString(messageMask) +
                ", mentionMask=" + toBinaryString(mentionMask) +
                ", messageLevelMask=" + toBinaryString(messageLevelMask )+
                ", mentionLevelMask=" + toBinaryString(mentionLevelMask) +
                '}';
    }

    @Override
    public String getIcon() {
        return Icons.YOUTUBE.getUrl();
    }

    @Override
    public String getUserId() {
        return channelId;
    }

}
