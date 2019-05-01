package cn.sola97.bot.livenotification.commands.channel;


import com.jagrosh.jdautilities.command.CommandEvent;
import cn.sola97.bot.livenotification.Bot;
import cn.sola97.bot.livenotification.commands.ChannelCommand;

public class RemoveCmd extends ChannelCommand {
    public RemoveCmd(Bot bot) {
        super(bot);
        this.name = "rm";
        this.arguments = "<platform@userId | url>";
        this.help = "remove a observer on TextChannel";
        this.aliases = new String[]{"remove", "del"};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = liveArgsExtract(event);
        if (args == null) return;


        switch (bot.getObManager().deleteObserver(event.getChannel().getId(), args[0], args[1])) {
            case SUCCESSED:
                event.replySuccess("删除**" + args[0] + "@" + args[1] + "**成功");
                break;
            case ALREADY_DELETED:
                event.reply("**" + args[0] + "@" + args[1] + "**不存在");
                break;
            case FAILED:
                event.replyError("删除**" + args[0] + "@" + args[1] + "**失败");
                break;
            default:
                event.reply("删除**" + args[0] + "@" + args[1] + "**结果未知");
        }
    }
}
