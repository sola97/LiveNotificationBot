package cn.sola97.bot.livenotification.commands.channel;

import com.jagrosh.jdautilities.command.CommandEvent;
import cn.sola97.bot.livenotification.Bot;
import cn.sola97.bot.livenotification.BotConfig;
import cn.sola97.bot.livenotification.commands.ChannelCommand;
import net.dv8tion.jda.core.entities.IMentionable;

import java.util.List;
import java.util.stream.Collectors;


public class SubCmd extends ChannelCommand {
    public SubCmd(Bot bot) {
        super(bot);
        this.name = "sub";
        this.help = "subscribe a streamer on TextChannel ";
        this.arguments = "<platform@userId | url> [@someone...]";
        this.aliases = new String[]{"add", "subscribe"};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = liveArgsExtract(event);
        List<String> mentions = event.getMessage().getMentionedUsers().stream().map(IMentionable::getAsMention).collect(Collectors.toList());
        if (args.length == 2)
            event.reply("正在检测直播间是否有效...", m -> {
                switch (bot.getObManager().subscribe(event.getChannel().getId(), args[0], args[1])) {
                    case SUCCESSED:
                        if (!mentions.isEmpty()) {
                            switch (bot.getObManager().addMentions(event.getChannel().getId(), args[0], args[1], mentions)) {
                                case SUCCESSED:
                                    m.editMessage("特别关注**" + args[0] + "@" + args[1] + "**成功").queue();
                                    break;
                            }
                        } else m.editMessage("订阅**" + args[0] + "@" + args[1] + "**成功").queue();
                        break;
                    case ALREADY_SUBSCRIBED:
                        if (!mentions.isEmpty()) {
                            switch (bot.getObManager().addMentions(event.getChannel().getId(), args[0], args[1], mentions)) {
                                case SUCCESSED:
                                    m.editMessage("添加提醒成功").queue();
                                    break;
                                case OBSERVER_NOT_FOUND:
                                    m.editMessage("没有找到**" + args[0] + "@" + args[1] + "**,如果要添加，请用" + BotConfig.getPrefix() + "sub 命令").queue();
                                    break;
                                case EMPTY_MENTIONS:
                                    m.editMessage("没有指定要提醒的用户").queue();
                                    break;
                                case ALREADY_EXISTS_MENTIONS:
                                    m.editMessage("要提醒的用户已存在").queue();
                                    break;
                                default:
                                    m.editMessage("添加失败，内部错误").queue();
                            }
                        } else m.editMessage("**" + args[0] + "@" + args[1] + "**已经是订阅状态").queue();
                        break;
                    case FAILED:
                        m.editMessage("订阅**" + args[0] + "@" + args[1] + "**失败").queue();
                        ;
                        break;
                    case INVALID_USER:
                        m.editMessage("订阅的用户**" + args[0] + "@" + args[1] + "**不存在").queue();
                        ;
                        break;
                    default:
                        m.editMessage("订阅**" + args[0] + "@" + args[1] + "**结果未知").queue();
                        ;
                        break;
                }
            });
        else
            event.reply("请检查参数");

    }
}
