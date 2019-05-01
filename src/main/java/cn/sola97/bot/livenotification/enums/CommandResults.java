package cn.sola97.bot.livenotification.enums;

public enum CommandResults {
    SUCCESSED(),
    OBSERVER_NOT_FOUND(),
    OBSERVABLE_NOT_FOUND(),
    ALREADY_SUBSCRIBED(),
    ALREADY_UNSUBSCRIBED(),
    ALREADY_DELETED(),
    FAILED(),
    VALID_USER(),
    INVALID_USER(),
    CHANNEL_NOT_EXISTS(),
    EMPTY_MENTIONS(),
    ALREADY_EXISTS_MENTIONS(),
    ALREADY_REMOVED_MENTIONS();
}
