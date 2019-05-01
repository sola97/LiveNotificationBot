package cn.sola97.bot.livenotification.pojo;

import net.dv8tion.jda.core.MessageBuilder;

import java.io.Serializable;

public class MessageDTO implements Serializable {
    public String channelId;
    public MessageBuilder messageBuilder;

    public MessageDTO(String channelId, MessageBuilder messageBuilder) {
        this.channelId = channelId;
        this.messageBuilder = messageBuilder;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "channelId='" + channelId + '\'' +
                ", messageBuilder=" + messageBuilder +
                '}';
    }
}
