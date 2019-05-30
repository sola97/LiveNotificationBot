package cn.sola97.bot.livenotification.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtil {
    public static String urlPatternString = "https://(?:www|live)?\\.?(bilibili|twitch|youtube)\\.(?:tv|com)/(?:channel/)?([\\w-]+)(?:/live)?";
    public static String inputPattenString = "(bilibili|twitch|youtube|observer)[@\\s*]([\\w-]+)";
    public static String proxyPatternString = "(http|socks)[5s]?://(.+):(\\d+)";
    public static Pattern inputPattern = Pattern.compile(inputPattenString,Pattern.CASE_INSENSITIVE);
    public static Pattern urlPattern = Pattern.compile(urlPatternString,Pattern.CASE_INSENSITIVE);
    public static Pattern proxyPattern = Pattern.compile(proxyPatternString,Pattern.CASE_INSENSITIVE);

    public static String[] parseInput(String inputString){
        Matcher m = inputPattern.matcher(inputString);
        if(m.find())
            return new String[] {m.group(1),m.group(2)};
        return new String[0];
    }

    public static String[] parseUrl(String urlString){
        Matcher m = urlPattern.matcher(urlString);
        if(m.find())
            return new String[] {m.group(1),m.group(2)};
        return new String[0];
    }
    public static String[] parseProxy(String proxyString){
        Matcher m = proxyPattern.matcher(proxyString);
        if(m.find())
            return new String[] {m.group(1),m.group(2),m.group(3)};
        return new String[0];
    }
}
