package cn.sola97.bot.livenotification.enums;

import java.util.function.BiFunction;

public enum LiveEvent {
    INIT(0b1111, "Bot正在监视%s的[直播间](%s)"),
    NONE(0b0000, "没有变化"),
    OPEN(0b0001, "%s开启了[直播](%s)"),
    CLOSE(0b0010, "%s关闭了[直播](%s)"),
    TITLE_CHANGED(0b0100, "%s更改了[直播间](%s)的标题"),
    THUMBNAIL_CHANGED(0b1000, "%s更改了[直播间](%s)的封面");


    private String description;
    public int mask;

    LiveEvent(int mask, String description) {
        this.mask = mask;
        this.description = description;
    }

    public static BiFunction<String, String, String> getDescriptionFuncByMask(int mask) {
        BiFunction<String, String, String> func = null;
        switch (mask) {
            case 0b0001:
                func = (name, url) -> String.format(LiveEvent.OPEN.description, name, url);
                break;
            case 0b0010:
                func = (name, url) -> String.format(LiveEvent.CLOSE.description, name, url);
                break;
            case 0b0100:
                func = (name, url) -> String.format(LiveEvent.TITLE_CHANGED.description, name, url);
                break;
            case 0b1000:
                func = (name, url) -> String.format(LiveEvent.THUMBNAIL_CHANGED.description, name, url);
                break;
            case 0b1111:
                func = (name, url) -> String.format(LiveEvent.INIT.description, name, url);
                break;
            case 0b0000:
                func = (name, url) -> null;
                break;
            default:
                func = (name, url) -> null;
        }
        return func;
    }

    public static int levelToMask(int level) {
        if (0 <= level && level <= 4) {
            return (int) Math.pow(2, level) - 1;
        }
        return -1;
    }

    public static int stringToMask(String mask) {
        int binMask = Integer.parseInt(mask, 2);
        if (binMask <= 16 && binMask >= 0) {
            return binMask;
        }
        return -1;
    }

    public static String getTextByMask(int mask, String eventName) {
        String text = "";
        text += (mask & OPEN.mask) == OPEN.mask ? "开播" + eventName + "\n" : "";
        text += (mask & CLOSE.mask) == CLOSE.mask ? "下播" + eventName + "\n" : "";
        text += (mask & TITLE_CHANGED.mask) == TITLE_CHANGED.mask ? "改标题" + eventName + "\n" : "";
        text += (mask & THUMBNAIL_CHANGED.mask) == THUMBNAIL_CHANGED.mask ? "改封面" + eventName + "\n" : "";

        return text;
    }
}


