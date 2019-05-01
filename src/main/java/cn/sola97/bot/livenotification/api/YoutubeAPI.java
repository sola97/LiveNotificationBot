package cn.sola97.bot.livenotification.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cn.sola97.bot.livenotification.pojo.impl.YoutubeDTO;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeAPI extends BaseAPI {
    private static final Logger logger = LoggerFactory.getLogger(YoutubeAPI.class);
    private static final String BASE_URL = "https://www.youtube.com/channel/%s/live";
    private static Pattern playerPattern = Pattern.compile("ytplayer.config = (.+?);ytplayer.load", Pattern.DOTALL);
    private static Pattern initDataPattern = Pattern.compile("ytInitialData\"] = (.+);");

    public static CompletableFuture<Optional<YoutubeDTO>> getChannel(String channelId) {
        CallbackFuture future = new CallbackFuture();
        Request request = new Request.Builder().url(String.format(BASE_URL, channelId)).
                addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36").build();
        okHttpClient.newCall(request).enqueue(future);
        return future.thenApply(
                response -> {
                    try {
                        String body = response.body().string();
                        Matcher macher = playerPattern.matcher(body);
                        if (macher.find()) {
                            String playerReponse = mapper.readTree(macher.group(1)).get("args").get("player_response").asText().replaceAll("\\\"", "\"");
                            playerReponse = putMetadataNode(body, playerReponse);
                            return Optional.of(mapper.readerFor(YoutubeDTO.class).readValue(playerReponse));
                        } else {
                            String playerReponse = putMetadataNode(body, "{}");
                            return Optional.of(mapper.readerFor(YoutubeDTO.class).readValue(playerReponse));
                        }
                    } catch (Exception e) {
                        logger.error("获取player_response出错", e);
                    } finally {
                        response.close();
                    }
                    return Optional.empty();
                }
        );
    }

    private static String putMetadataNode(String Html, String playerResponse) throws IOException {
        Matcher macher = initDataPattern.matcher(Html);
        if (macher.find()) {
            String InitData = macher.group(1);
            JsonNode initData = mapper.readTree(InitData);
            ObjectNode player = (ObjectNode) mapper.readTree(playerResponse);
            try {
                // 当直播间不存在时，自动跳转channel页面,获取channelMetadataRenderer
                Optional<JsonNode> channelMetaData = Optional.ofNullable(initData.get("metadata")).map(n -> n.get("channelMetadataRenderer"));
                if (channelMetaData.isPresent()) {
                    player.set("profile", channelMetaData.get().get("avatar").get("thumbnails").get(0).get("url"));
                    player.set("channelMetadataRenderer", channelMetaData.get());
                } else {
                    //当直播间存在时候,收集头像和分区信息
                    JsonNode profile = initData.findValue("videoOwnerRenderer").get("thumbnail").findValues("url").get(2);
                    JsonNode category = initData.findValue("metadataRowRenderer").get("contents").findValue("text");
                    player.set("profile", profile);
                    player.set("category", category);
                }
            } catch (Exception e) {
                logger.error("没有找到节点", e);
                return playerResponse;
            }
            return player.toString();
        }
        return playerResponse;
    }

    static String getUrl(String channelId) {
        return String.format(BASE_URL, channelId);
    }
}
