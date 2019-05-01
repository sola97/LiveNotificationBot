package cn.sola97.bot.livenotification.observer;

import cn.sola97.bot.livenotification.enums.Colors;
import cn.sola97.bot.livenotification.enums.Icons;

public class BilibiliObserver extends BaseObserver {

    private String roomId;

    public BilibiliObserver(String channelId, String roomId) {
        super(channelId);
        this.roomId = roomId;
    }


    @Override
    public String toString() {
        return "BilibiliObserver{" +
                "roomId='" + roomId + '\'' +
                ", enabled=" + enabled +
                ", liveDTO=" + liveDTO +
                ", discordChannelId='" + discordChannelId + '\'' +
                ", mentions=" + mentions +
                ", messageMask=" + toBinaryString(messageMask) +
                ", mentionMask=" + toBinaryString(mentionMask) +
                ", messageLevelMask=" + toBinaryString(messageLevelMask) +
                ", mentionLevelMask=" + toBinaryString(mentionLevelMask) +
                '}';
    }

    @Override
    public int getEmbedCorlor() {
        return Colors.BILIBILI_BLUE.value;
    }

    @Override
    public String getIcon() {
        return Icons.BILIBILI.getUrl();
    }

    @Override
    public String getUserId() {
        return roomId;
    }

}
