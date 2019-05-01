package cn.sola97.bot.livenotification.api;

import cn.sola97.bot.livenotification.enums.CommandResults;
import cn.sola97.bot.livenotification.utils.OkHttpUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

abstract public class BaseAPI {

    private static final Logger logger = LoggerFactory.getLogger(BaseAPI.class);

    static final OkHttpClient okHttpClient = OkHttpUtil.getInstance();
    static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    protected static class CallbackFuture extends CompletableFuture<Response> implements Callback {
        public void onResponse(@NotNull Call call, @NotNull Response response) {
            super.complete(response);
            logger.debug(response.request().url() + " HTTP:" + response.code());
        }

        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            super.completeExceptionally(e);
            logger.error("Request onFailure " + e.getClass().getName() + "URL:" + call.request().url());
        }
    }

    static public CompletableFuture<CommandResults> checkValid(String type, String urlKey) {
        String url;
        switch (type.toLowerCase()) {
            case "bilibili":
                url = BilibiliAPI.getUrl(urlKey);
                break;
            case "twitch":
                url = TwitchAPI.getUrl(urlKey);
                break;
            case "youtube":
                url = YoutubeAPI.getUrl(urlKey);
                break;
            default:
                return CompletableFuture.supplyAsync(() -> CommandResults.INVALID_USER);
        }
        CallbackFuture future = new CallbackFuture();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(future);
        return future.thenApply(Response::isSuccessful).thenApply(valid -> valid ? CommandResults.VALID_USER : CommandResults.INVALID_USER);
    }

}
