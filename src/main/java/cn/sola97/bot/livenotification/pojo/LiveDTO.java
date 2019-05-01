package cn.sola97.bot.livenotification.pojo;

import cn.sola97.bot.livenotification.enums.LiveEvent;
import cn.sola97.bot.livenotification.enums.LiveStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.Serializable;

public interface LiveDTO extends Serializable {
    @NotNull String getTitle(); //标题

    @NotNull LiveStatus getLiveStatus();   //直播状态

    @NotNull LiveEvent getLiveEvent();

    String getUserName(); //用户名

    String getArea();    //分区

    String getProfile();   //头像

    String getThumbnail();   //封面

    String getVideoUrl();

    String getChannelUrl();

    LiveDTO setLiveEvent(LiveEvent event);

    LiveDTO setLiveStatus(LiveStatus status);
}
