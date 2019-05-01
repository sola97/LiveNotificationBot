package cn.sola97.bot.livenotification.enums;

public enum Icons {
    TWITCH("https://i.loli.net/2019/04/23/5cbf2f1b9318c.png"),
    YOUTUBE("https://i.loli.net/2019/04/23/5cbf2f1bc2c2e.png"),
    BILIBILI("https://i.loli.net/2019/04/23/5cbf2f1bc9cbc.png");

    private String url;

    Icons(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
