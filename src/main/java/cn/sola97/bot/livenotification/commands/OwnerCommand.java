package cn.sola97.bot.livenotification.commands;

import com.jagrosh.jdautilities.command.Command;

public abstract class OwnerCommand extends Command {
    public OwnerCommand() {
        this.category = new Category("Owner");
        this.ownerCommand = true;
    }
}
