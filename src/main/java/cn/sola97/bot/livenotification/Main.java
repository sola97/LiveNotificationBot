package cn.sola97.bot.livenotification;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cn.sola97.bot.livenotification.commands.channel.*;
import cn.sola97.bot.livenotification.commands.owner.ShutdownCmd;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("Start up");

        ObserverManager manager = new ObserverManager();
        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder client = new CommandClientBuilder();
        Bot bot = new Bot(waiter, manager);
        client.useDefaultGame()
                .setOwnerId(BotConfig.getOwnerId())
                .setPrefix(BotConfig.getPrefix())
                .addCommands(
                        new SubCmd(bot),
                        new UnsubCmd(bot),
                        new RemoveCmd(bot),
                        new ShowCmd(bot),
                        new MentionCmd(bot),
                        new SetCmd(bot),
                        new ShutdownCmd(bot)
                );

        try {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setHttpClientBuilder(
                            new OkHttpClient.Builder()
                            .proxy(BotConfig.getProxy())
                    )
                    .setToken(BotConfig.getToken())
                    .setGame(Game.playing("loading..."))
                    .addEventListener(waiter)
                    .addEventListener(client.build())
                    .addEventListener(new Listener(bot))
                    .build();
            bot.setJDA(jda);
        } catch (LoginException ex) {
            logger.error(ex + "\nPlease make sure you are "
                    + "editing the correct config.txt file, and that you have used the "
                    + "correct token (not the 'secret'!)");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
