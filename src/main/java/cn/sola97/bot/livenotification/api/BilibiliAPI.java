package cn.sola97.bot.livenotification.api;

import cn.sola97.bot.livenotification.pojo.impl.BilibiliDTO;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BilibiliAPI extends BaseAPI {
    private static final Logger logger = LoggerFactory.getLogger(BilibiliAPI.class);
    private static final String BASE_URL = "http://api.live.bilibili.com/AppRoom/index?room_id=%s&platform=android";

    public static CompletableFuture<Optional<BilibiliDTO>> getRoom(String roomId) {
        CallbackFuture future = new CallbackFuture();
        Request request = new Request.Builder().url(String.format(BASE_URL, roomId)).build();
        okHttpClient.newCall(request).enqueue(future);
        return future.thenApply(
                response -> {
                    try {
                        JsonNode all = mapper.readTree(response.body().string());
                        if (all.hasNonNull("data")) {
                            JsonNode data = all.get("data");
                            return Optional.of(mapper.readerFor(BilibiliDTO.class).readValue(data));
                        }
                    } catch (Exception e) {
                        logger.error("获取data出错", e);
                    } finally {
                        response.close();
                    }
                    return Optional.empty();
                });
    }

    static String getUrl(String roomId) {
        return String.format(BASE_URL, roomId);
    }
}
