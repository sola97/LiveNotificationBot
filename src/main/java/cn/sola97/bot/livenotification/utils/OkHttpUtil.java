package cn.sola97.bot.livenotification.utils;

import cn.sola97.bot.livenotification.BotConfig;
import okhttp3.OkHttpClient;

public class OkHttpUtil
{
    private static OkHttpClient singleton;
    private OkHttpUtil(){

    }
    public static OkHttpClient getInstance() {
        if (singleton == null)
        {
            synchronized (OkHttpUtil.class)
            {
                if (singleton == null)
                {
                    singleton = new OkHttpClient()
                            .newBuilder()
                            .proxy(BotConfig.getProxy())
                            .build();
                }
            }
        }
        return singleton;
    }
}