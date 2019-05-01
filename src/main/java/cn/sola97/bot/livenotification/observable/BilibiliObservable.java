package cn.sola97.bot.livenotification.observable;

import cn.sola97.bot.livenotification.api.BilibiliAPI;
import cn.sola97.bot.livenotification.pojo.impl.BilibiliDTO;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BilibiliObservable extends BaseObservable {
    private String roomId;

    public BilibiliObservable(String roomId) {
        super();
        this.roomId = roomId;
    }


    @Override
    protected String getLiveId() {
        return roomId;
    }

    @Override
    protected CompletableFuture<Optional<BilibiliDTO>> getPage() {
        return BilibiliAPI.getRoom(roomId);
    }
}