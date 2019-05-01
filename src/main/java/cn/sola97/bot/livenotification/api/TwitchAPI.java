package cn.sola97.bot.livenotification.api;

import com.fasterxml.jackson.databind.JsonNode;
import cn.sola97.bot.livenotification.enums.LiveStatus;
import cn.sola97.bot.livenotification.pojo.impl.TwitchDTO;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TwitchAPI extends BaseAPI {
    private static final Logger logger = LoggerFactory.getLogger(TwitchAPI.class);
    private static final String CLIENT_ID = "jzkbprff40iqj646a697cyrvl0zt2m6";
    private static final String STREAM_URL = "https://api.twitch.tv/kraken/streams/%s?client_id=" + CLIENT_ID + "&Accept=application/json";
    private static final String CHANNEL_URL = "https://api.twitch.tv/kraken/channels/%s?client_id=" + CLIENT_ID + "&Accept=application/json";

    public static CompletableFuture<Optional<TwitchDTO>> getStreams(String userId) {
        CallbackFuture future = new CallbackFuture();
        Request request = new Request.Builder().url(String.format(STREAM_URL, userId)).build();
        okHttpClient.newCall(request).enqueue(future);
        return future.thenApply(
                response -> {
                    try {
                        JsonNode all = mapper.readTree(response.body().string());
                        if (all.has("stream")) {
                            JsonNode data = all.get("stream");
                            if (data.isNull())
                                return Optional.of(new TwitchDTO().setLiveStatus(LiveStatus.CLOSED));
                            TwitchDTO twitchDTO = mapper.readerFor(TwitchDTO.class).readValue(data);
                            return Optional.of(twitchDTO);
                        }
                    } catch (Exception e) {
                        logger.error("TwitchAPI@" + userId + "getStreams() Error:", e);
                    } finally {
                        response.body().close();
                    }
                    return Optional.empty();
                });
    }

    public static CompletableFuture<Optional<TwitchDTO>> getChannel(String userId) {
        CallbackFuture future = new CallbackFuture();
        Request request = new Request.Builder().url(String.format(CHANNEL_URL, userId)).build();
        okHttpClient.newCall(request).enqueue(future);
        return future.thenApply(
                response -> {
                    try {
                        TwitchDTO twitchDTO = mapper.readerFor(TwitchDTO.class).readValue(mapper.readTree("{\"channel\":" + response.body().string() + "}"));
                        return Optional.of(twitchDTO);
                    } catch (Exception e) {
                        logger.warn("TwitchAPI@" + userId + "getChannel Error");
                    } finally {
                        response.close();
                    }
                    return Optional.empty();
                });
    }


    static String getUrl(String userId) {
        return String.format(CHANNEL_URL, userId);
    }

}
