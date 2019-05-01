package cn.sola97.bot.livenotification.observable;

import cn.sola97.bot.livenotification.api.YoutubeAPI;
import cn.sola97.bot.livenotification.pojo.impl.YoutubeDTO;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class YoutubeObservable extends BaseObservable {
    private String channelId;

    public YoutubeObservable(String channelId) {
        super();
        this.channelId = channelId;
    }

    @Override
    protected String getLiveId() {
        return channelId;
    }

    @Override
    protected CompletableFuture<Optional<YoutubeDTO>> getPage() {
        return YoutubeAPI.getChannel(channelId);
    }
}