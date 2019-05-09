package cn.sola97.bot.livenotification;

import cn.sola97.bot.livenotification.utils.ParseUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BotConfig {
    private static Path path;
    private static final Logger logger = LoggerFactory.getLogger(BotConfig.class);
    private static final String token, proxy, prefix, ownerId,game;

    public static final String BIG_LEFT = "⏪";
    public static final String LEFT = "◀";
    public static final String STOP = "\u23f9";
    public static final String RIGHT = "▶";
    public static final String BIG_RIGHT = "⏩";
    public static final String SUBSCRIBE = "❤";
    public static final String UNSUBSCRIBE = "💔";
    public static final String MENTION = "⏰";
    public static final String REMOVE_MENTION = "\uD83D\uDD07";
    public static final String CHANGE_MENTION_LEVEL = "\uD83D\uDD02";
    public static final String CHANGE_MSG_LEVEL = "\uD83D\uDD01";
    public static final String DELETE = "✖";

    static {
        path = Paths.get(System.getProperty("config.file", System.getProperty("config", "config.txt")));
        if(path.toFile().exists())
        {
            if(System.getProperty("config.file") == null)
                System.setProperty("config.file", System.getProperty("config", "config.txt"));
            ConfigFactory.invalidateCaches();
        }
        Config config = ConfigFactory.load();
        token = config.getString("token");
        proxy =  config.getString("proxy");
        prefix = config.getString("prefix");
        ownerId = config.getString("owner");
        game = config.getString("game");
        if(token==null||token.isEmpty()){
            logger.error("Please provide a bot token.");
            System.exit(1);
        }
        if(ownerId==null||ownerId.isEmpty()){
            logger.error("Please provide a ownerId.");
            System.exit(1);
        }
    }

    public static Proxy getProxy() {
        if(proxy==null || proxy.isEmpty())return null;
        String[] args = ParseUtil.parseProxy(proxy);
        if(args.length==0){
            logger.warn("ProxyURL is not correct.");
        }
        switch (args[0].toUpperCase()){
            case "HTTP":
                return new Proxy(Proxy.Type.HTTP,new InetSocketAddress(args[1],Integer.parseInt(args[2])));
            case "SOCKS":
                return new Proxy(Proxy.Type.SOCKS,new InetSocketAddress(args[1],Integer.parseInt(args[2])));
            default:
                logger.warn("unsupport proxy type, ignored.");
                return null;
        }
    }

    public static String getPrefix() {
        return prefix==null||prefix.isEmpty()?"!":prefix;
    }

    public static String getOwnerId() {
        return ownerId;

    }

    public static String getToken(){
        return token;
    }

    public static String getGame() {
        return game==null||game.isEmpty()?"BiliBili Youtube Twitch":game;
    }
}
