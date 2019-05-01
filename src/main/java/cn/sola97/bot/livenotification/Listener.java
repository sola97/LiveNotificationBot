package cn.sola97.bot.livenotification;

import cn.sola97.bot.livenotification.enums.Icons;
import cn.sola97.bot.livenotification.pojo.LiveDTO;
import cn.sola97.bot.livenotification.pojo.impl.YoutubeDTO;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;

public class Listener extends ListenerAdapter
{
    private final Bot bot;
    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

    Listener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onReady(ReadyEvent event) {
        bot.messageConsumerStart();
        bot.channelReaperStart();
    }
}