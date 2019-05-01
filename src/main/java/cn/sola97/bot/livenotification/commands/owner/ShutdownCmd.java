package cn.sola97.bot.livenotification.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import cn.sola97.bot.livenotification.Bot;
import cn.sola97.bot.livenotification.commands.OwnerCommand;

public class ShutdownCmd extends OwnerCommand {
    private final Bot bot;

    public ShutdownCmd(Bot bot) {
        this.bot = bot;
        this.name = "shutdown";
        this.help = "safely shuts down";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.replyWarning("Shutting down...");
        bot.shutdown();
    }
}
