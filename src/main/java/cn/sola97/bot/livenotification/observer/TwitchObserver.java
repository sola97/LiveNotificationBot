package cn.sola97.bot.livenotification.observer;

import cn.sola97.bot.livenotification.enums.Colors;
import cn.sola97.bot.livenotification.enums.Icons;

public class TwitchObserver extends BaseObserver {

    private String userId;

    public TwitchObserver(String discordChannelId, String liveChannelId) {
        super(discordChannelId);
        this.userId = liveChannelId;
    }

    @Override
    public String toString() {
        return "TwitchObserver{" +
                "userId='" + userId + '\'' +
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
    public int getEmbedCorlor() {
        return Colors.TWITCH_PURPLE.value;
    }

    @Override
    public String getIcon() {
        return Icons.TWITCH.getUrl();
    }

    @Override
    public String getUserId() {
        return userId;
    }

}
