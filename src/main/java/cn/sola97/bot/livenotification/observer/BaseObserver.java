package cn.sola97.bot.livenotification.observer;

import cn.sola97.bot.livenotification.BotConfig;
import cn.sola97.bot.livenotification.enums.CommandResults;
import cn.sola97.bot.livenotification.enums.LiveEvent;
import cn.sola97.bot.livenotification.pojo.LiveDTO;
import cn.sola97.bot.livenotification.pojo.MessageDTO;
import cn.sola97.bot.livenotification.utils.SingleBlockingQueue;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;

abstract public class BaseObserver implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(BaseObserver.class);
    private static final BlockingQueue<MessageDTO> queue = SingleBlockingQueue.getInstance();

    Boolean enabled = false;
    transient LiveDTO liveDTO;
    String discordChannelId;
    SortedSet<String> mentions;
    int messageMask = 0b0000;
    int mentionMask = 0b0000;
    int messageLevelMask = 0b0011;
    int mentionLevelMask = 0b0001;

    public abstract int getEmbedCorlor();

    public abstract String getIcon();

    public abstract String getUserId();

    BaseObserver(String discordChannelId) {
        this.discordChannelId = discordChannelId;
        this.mentions = new ConcurrentSkipListSet<>();
    }

    public String getClassName() {
        return String.format(this.getClass().getSimpleName() + "@[%s]#%s", getUserId(), discordChannelId);
    }

    public EmbedBuilder getEmbedBuilderShowAll() {
        EmbedBuilder embed = getBaseEmbedBuilder();
        embed.setDescription("当前状态");
        embed.addField("状态", isEnabled() ? BotConfig.SUBSCRIBE : BotConfig.UNSUBSCRIBE, true);
        embed.addField("@提醒", getMentions(), true);
        embed.addField("提醒等级", getMsgLevelDescription(), true);
        embed.addField("Mask:", getMsgLevelMaskText(), true);
        return embed;
    }

    public CommandResults setMessageMask(int messageMask) {
        this.messageMask = messageMask;
        return CommandResults.SUCCESSED;
    }

    public CommandResults setMentionMask(int mentionMask) {
        this.mentionMask = mentionMask;
        return CommandResults.SUCCESSED;
    }


    public Boolean isEnabled() {
        return enabled;
    }

    public BaseObserver setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getMentions() {
        return String.join("\n", mentions);
    }

    public String getMsgLevelDescription() {
        int msgMask = (messageMask | messageLevelMask) - ((messageMask | messageLevelMask) & (mentionMask | mentionLevelMask));
        //@提醒的部分 + 普通消息的部分
        return LiveEvent.getTextByMask(mentionMask | mentionLevelMask, "@提醒") +
                LiveEvent.getTextByMask(msgMask, "提醒");
    }

    protected String toBinaryString(int i) {
        return String.format("%4s", Integer.toBinaryString(i)).replace(" ", "0");
    }

    public String getMsgLevelMaskText() {
        String text = "mentions level:" + toBinaryString(mentionLevelMask) +
                "\n" + "message level:" + toBinaryString(messageLevelMask);
        if ((messageMask | mentionMask) != 0)
            text += "\nmention:" + toBinaryString(mentionMask)
                    + "\nmessage:" + toBinaryString(messageMask);
        return text;
    }


    public void update(LiveDTO DTO) {
        //用于addObserver后的update判断是否要发送INIT消息
        if (DTO.getLiveEvent() == LiveEvent.NONE && liveDTO == null)
            liveDTO = DTO.setLiveEvent(LiveEvent.INIT);
        else
            liveDTO = DTO;

        logger.info(getClassName() + "接收到通知\n" + liveDTO);
        getMessageDTO().ifPresent(this::put2Queue);
    }

    private Optional<String> getText() {
        if (liveDTO == null) return Optional.empty();
        if (liveDTO.getLiveEvent() == LiveEvent.INIT) return Optional.empty();
        if (((mentionMask | mentionLevelMask) & liveDTO.getLiveEvent().mask) != 0 && !getMentions().isEmpty())
            return Optional.of(getMentions());
        return Optional.empty();
    }

    public Optional<MessageDTO> getMessageDTO() {
        int mask = 0;
        //通过计算掩码来过滤非订阅消息
        EmbedBuilder embed = getBaseEmbedBuilder();
        if (liveDTO.getLiveEvent() == LiveEvent.INIT)
            mask = LiveEvent.INIT.mask;
        else
            // @提醒和普通消息
            mask = (mentionMask | mentionMask | messageMask | messageLevelMask) & liveDTO.getLiveEvent().mask;

        //添加event描述
        // 当mask==0时 getDescriptionFuncByMask为()->null Optional.map返回Optional.empty()
        return Optional.ofNullable(LiveEvent.getDescriptionFuncByMask(mask).apply(liveDTO.getUserName(), liveDTO.getChannelUrl()))
                .map(embed::setDescription)
                .map(e -> {
                            MessageBuilder mb = new MessageBuilder().setEmbed(embed.build());
                            getText().ifPresent(mb::setContent);
                            return new MessageDTO(discordChannelId, mb);
                        }
                );
    }

    public EmbedBuilder getBaseEmbedBuilder() {

        EmbedBuilder embed = new EmbedBuilder().setColor(getEmbedCorlor());
        //设置footer 分区
        embed.setFooter(Optional.ofNullable(liveDTO).map(LiveDTO::getArea).orElse("-"), getIcon());
        //设置时间
        embed.setTimestamp(Instant.now());

        if (liveDTO == null) return embed;

        //设置name/url/icon @Nullable
        embed.setAuthor(liveDTO.getUserName(), liveDTO.getChannelUrl(), liveDTO.getProfile());

        //设置标题和直播 @NotNull
        embed.addField(liveDTO.getTitle(), liveDTO.getLiveStatus().getText(), false);

        //设置thumbnail @Nullable
        embed.setThumbnail(liveDTO.getThumbnail());

        return embed;
    }

    private void put2Queue(MessageDTO messageVO) {
        try {
            queue.put(messageVO);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CommandResults addMentions(String... userAsMentions) {
        if (mentions.addAll(Arrays.asList(userAsMentions)))
            return CommandResults.SUCCESSED;
        return CommandResults.ALREADY_EXISTS_MENTIONS;
    }

    public CommandResults removeMentions(String... userAsMentions) {
        if (mentions.removeAll(Arrays.asList(userAsMentions)))
            return CommandResults.SUCCESSED;
        return CommandResults.ALREADY_REMOVED_MENTIONS;
    }

    public CommandResults clearMentions() {
        mentions.clear();
        return CommandResults.SUCCESSED;
    }

    public static int levelUp(int mask) {
        if (mask << 1 == 0b11110) return 0b0000;
        return (mask << 1) | 0b0001;
    }

    public CommandResults changeMentionLevel() {
        mentionLevelMask = levelUp(mentionLevelMask);
        return CommandResults.SUCCESSED;
    }

    public CommandResults changeMessageLevel() {
        messageLevelMask = levelUp(messageLevelMask);
        return CommandResults.SUCCESSED;
    }

    public CommandResults setMessageLevelMask(int messageLevelMask) {
        this.messageLevelMask = messageLevelMask;
        return CommandResults.SUCCESSED;
    }

    public CommandResults setMentionLevelMask(int mentionLevelMask) {
        this.mentionLevelMask = mentionLevelMask;
        return CommandResults.SUCCESSED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseObserver observer = (BaseObserver) o;
        return messageMask == observer.messageMask &&
                Objects.equals(enabled, observer.enabled) &&
                Objects.equals(discordChannelId, observer.discordChannelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, discordChannelId, mentions, messageMask);
    }
}

