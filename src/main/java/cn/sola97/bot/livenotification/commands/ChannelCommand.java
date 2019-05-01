package cn.sola97.bot.livenotification.commands;


import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import cn.sola97.bot.livenotification.Bot;
import cn.sola97.bot.livenotification.utils.ParseUntil;

public abstract class ChannelCommand extends Command {
    protected final Bot bot;

    public ChannelCommand(Bot bot) {
        this.category = new Category("Live");
        this.guildOnly = true;
        this.bot = bot;
    }

    protected String[] liveArgsExtract(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyError("参数不能为空");
            return null;
        }
        String[] args = ParseUntil.parseInput(event.getArgs());
        if (args.length != 0) return args;

        args = ParseUntil.parseUrl(event.getArgs());
        if (args.length != 0) return args;

        return null;
    }
}