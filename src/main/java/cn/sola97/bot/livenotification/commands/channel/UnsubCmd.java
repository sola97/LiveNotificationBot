package cn.sola97.bot.livenotification.commands.channel;

import com.jagrosh.jdautilities.command.CommandEvent;
import cn.sola97.bot.livenotification.Bot;
import cn.sola97.bot.livenotification.commands.ChannelCommand;

public class UnsubCmd extends ChannelCommand {
    public UnsubCmd(Bot bot) {
        super(bot);
        this.name = "unsub";
        this.help = "unsubscribe a streamer on TextChannel ";
        this.arguments = "<platform@userId | url>";
        this.aliases = new String[]{"unsubscribe"};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = liveArgsExtract(event);
        if (args == null) return;
        switch (bot.getObManager().unsubscribe(event.getChannel().getId(), args[0], args[1])) {
            case SUCCESSED:
                event.replySuccess("取消订阅**" + args[0] + "@" + args[1] + "**成功");
                break;
            case ALREADY_UNSUBSCRIBED:
                event.reply("**" + args[0] + "@" + args[1] + "**已经是取消状态");
                break;
            case FAILED:
                event.replyError("取消订阅**" + args[0] + "@" + args[1] + "**失败");
                break;
            default:
                event.reply("取消订阅**" + args[0] + "@" + args[1] + "**结果未知");
        }

    }
}
