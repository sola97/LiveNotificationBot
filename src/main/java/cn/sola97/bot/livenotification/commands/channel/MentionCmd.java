package cn.sola97.bot.livenotification.commands.channel;

import com.jagrosh.jdautilities.command.CommandEvent;
import cn.sola97.bot.livenotification.Bot;
import cn.sola97.bot.livenotification.BotConfig;
import cn.sola97.bot.livenotification.commands.ChannelCommand;
import net.dv8tion.jda.core.entities.IMentionable;

import java.util.List;
import java.util.stream.Collectors;


public class MentionCmd extends ChannelCommand {
    public MentionCmd(Bot bot) {
        super(bot);
        this.name = "mention";
        this.help = "manage mentions for a subscribed streamer.";
        this.arguments = "<add | remove | clear> [@self...] <platform@userId | url>";
        this.aliases = new String[]{"mentions"};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = liveArgsExtract(event);
        List<String> mentions = event.getMessage().getMentionedUsers().stream().map(IMentionable::getAsMention).collect(Collectors.toList());
        if (mentions.isEmpty()) mentions.add(event.getMessage().getAuthor().getAsMention());
        if (args == null) {
            event.reply("直播间参数有误");
            return;
        }
        ;
        String[] action = event.getArgs().split("\\s+", 2);
        switch (action[0]) {
            case "add":
                switch (bot.getObManager().addMentions(event.getChannel().getId(), args[0], args[1], mentions)) {
                    case SUCCESSED:
                        event.replySuccess("添加提醒成功");
                        break;
                    case OBSERVER_NOT_FOUND:
                        event.replyError("没有找到**" + args[0] + "@" + args[1] + "**，请先添加");
                        break;
                    case EMPTY_MENTIONS:
                        event.reply("没有指定要提醒的用户");
                        break;
                    case ALREADY_EXISTS_MENTIONS:
                        event.reply("要提醒的用户已存在");
                        break;
                    default:
                        event.replyError("添加失败，内部错误");
                }
                break;
            case "rm":
            case "remove":
                switch (bot.getObManager().removeMentions(event.getChannel().getId(), args[0], args[1], mentions)) {
                    case SUCCESSED:
                        event.replySuccess("移除提醒成功");
                        break;
                    case OBSERVER_NOT_FOUND:
                        event.replyError("要移除提醒的**" + args[0] + "@" + args[1] + "**直播间不存在");
                        break;
                    case EMPTY_MENTIONS:
                        event.reply("没有指定移除的用户");
                        break;
                    case ALREADY_REMOVED_MENTIONS:
                        event.reply("不在" + args[0] + "@" + args[1] + "的提醒列表中");
                        break;
                    default:
                        event.replyError("移除失败，内部错误");
                }
                break;
            case "clr":
            case "clear":
                switch (bot.getObManager().clearMentions(event.getChannel().getId(), args[0], args[1])) {
                    case SUCCESSED:
                        event.replySuccess("清空提醒成功");
                        break;
                    case OBSERVER_NOT_FOUND:
                        event.replyError("要清空提醒的**" + args[0] + "@" + args[1] + "**直播间不存在");
                        break;
                    default:
                        event.replyError("清空提醒失败，内部错误");
                }
                break;
            default:
                event.replyError("操作" + action[0] + "不支持，请检查命令");
        }
    }
}


