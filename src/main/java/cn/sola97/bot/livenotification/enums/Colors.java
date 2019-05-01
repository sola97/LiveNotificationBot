package cn.sola97.bot.livenotification.enums;

public enum Colors {
    YOUTUBE_RED(0xff0000),
    BILIBILI_BLUE(0x24ACE6),
    TWITCH_PURPLE(0x6441A4);

    public int value;

    Colors(int value) {
        this.value = value;
    }
}
