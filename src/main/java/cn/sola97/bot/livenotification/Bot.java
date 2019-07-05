package cn.sola97.bot.livenotification;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cn.sola97.bot.livenotification.enums.CommandResults;
import cn.sola97.bot.livenotification.pojo.MessageDTO;
import cn.sola97.bot.livenotification.utils.SingleBlockingQueue;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.*;

public class Bot {
    protected static final Logger logger = LoggerFactory.getLogger(Bot.class);
    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final EventWaiter waiter;
    private final ObserverManager obManager;
    private JDA jda;
    private ConcurrentHashMap<String, Integer> channelNotExsitsCount;

    public Bot(EventWaiter waiter, ObserverManager obManager) {
        this.waiter = waiter;
        this.obManager = obManager;
        channelNotExsitsCount = new ConcurrentHashMap<>();
    }

    public EventWaiter getWaiter() {
        return waiter;
    }

    public ObserverManager getObManager() {
        return obManager;
    }

    public JDA getJDA() {
        return jda;
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }

    public void shutdown() {
        logger.info("shutdown ...");
        obManager.shutdown();
        jda.shutdown();
        logger.info("shutdown - finished");
        System.exit(0);
    }

    public void messageConsumerStart() {
        Thread t = new Thread(() -> {
            BlockingQueue<MessageDTO> queue = SingleBlockingQueue.getInstance();
            while (true) {
                try {
                    MessageDTO dto = queue.take();
                    Optional.ofNullable(jda.getTextChannelById(dto.channelId)).map(channel -> {
                        Message msg = dto.messageBuilder.build();
                        channel.sendMessage(msg)
                                .queue(suc -> logger.info("发送成功：" + msg.getEmbeds().get(0).getDescription()), fail ->logger.warn("发送失败：" + msg.getEmbeds().get(0).getDescription(),fail));
                        return CommandResults.SUCCESSED;
                    }).orElseGet(() -> {
                        return CommandResults.CHANNEL_NOT_EXISTS;
                    });
                } catch (Exception e) {
                    logger.warn("MessageDTO消费者：", e);
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void channelReaperStart() {
        executor.scheduleAtFixedRate(() -> {
            getObManager().getChannels().forEach(channelId -> {
                        if (jda.getTextChannelById(channelId) == null) {
                            int counts = channelNotExsitsCount.compute(channelId, (channel, count) -> count == null ? 1 : count + 1);
                            logger.info("Channel:" + channelId + "不存在, 当前计数" + counts);
                            if (counts > 36) { //连续6小时不存在
                                logger.info("删除不存在的Channel：" + channelId);
                                getObManager().deleteChannel(channelId);
                            }
                        }else {
                            if(channelNotExsitsCount.containsKey(channelId)){
                                channelNotExsitsCount.remove(channelId);
                                logger.info("Channel:"+channelId+"获取成功，清空计数");
                            }
                        }
                    }
            );
        },1,10, TimeUnit.MINUTES);
    }
}
