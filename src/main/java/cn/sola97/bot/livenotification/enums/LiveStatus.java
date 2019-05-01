package cn.sola97.bot.livenotification.enums;

public enum LiveStatus {
    OPENED("正在直播"),
    CLOSED("闲置中"),
    ROUNDED("轮播中"),
    UNKNOWN("???");
    private String text;

    LiveStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
