package cn.sola97.bot.livenotification.utils;

import cn.sola97.bot.livenotification.pojo.MessageDTO;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SingleBlockingQueue
{
    private static BlockingQueue<MessageDTO> singleton ;

    private SingleBlockingQueue(){

    }
    public static BlockingQueue<MessageDTO> getInstance() {
        if (singleton == null)
        {
            synchronized (BlockingQueue.class)
            {
                if (singleton == null)
                {
                    singleton = new LinkedBlockingQueue<>();
                }
            }
        }
        return singleton;
    }
}