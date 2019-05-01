package cn.sola97.bot.livenotification.commands.channel;

import cn.sola97.bot.livenotification.enums.LiveEvent;
import com.jagrosh.jdautilities.command.CommandEvent;
import cn.sola97.bot.livenotification.Bot;
import cn.sola97.bot.livenotification.commands.ChannelCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetCmd extends ChannelCommand {
    private Pattern setPattern = Pattern.compile("(message|msg|mention|mentions)\\s*(level|mask)\\s*(\\d+)");

    public SetCmd(Bot bot) {
        super(bot);
        this.name = "set";
        this.help = "set message or mention level";
        this.arguments = "<message | mention> <level | mask> <0-4 | 0000-1111> <platform@userId | url> ";
    }

    @Override
    protected void execute(CommandEvent event) {

        int mask = -1;
        String[] args = liveArgsExtract(event);
        if (args.length == 0) {
            event.replyError("直播间参数有误");
            return;
        }
        Matcher matcher = setPattern.matcher(event.getArgs());
        if (!matcher.find()) {
            event.replyError("设置参数有误");
        }
        String action = matcher.group(1);
        String levelOrMask = matcher.group(2);
        String num = matcher.group(3);

        switch (levelOrMask) {
            case "level":
                mask = LiveEvent.levelToMask(Integer.parseInt(num));
                break;
            case "mask":
                mask = LiveEvent.stringToMask(num);
                break;
        }
        if (mask == -1) {
            event.replyError("请输入正确的level或mask");
            return;
        }
        Handler h = new Handler();
        switch (action) {
            case "mention":
            case "mentions":
                switch (levelOrMask) {
                    case "level":
                        h.setMentionLevel(event, args, num);
                        break;
                    case "mask":
                        h.setMentionMask(event, args, num);
                        break;
                }
                break;
            case "msg":
            case "message":
                switch (levelOrMask) {
                    case "level":
                        h.setMessageLevel(event, args, num);
                        break;
                    case "mask":
                        h.setMessageMask(event, args, num);
                        break;
                }
                break;
        }
    }

    class Handler {
        public void setMentionLevel(CommandEvent event, String[] args, String level) {
            int mask = LiveEvent.levelToMask(Integer.parseInt(level));
            switch (bot.getObManager().setMentionLevel(event.getChannel().getId(), args[0], args[1], mask)) {
                case SUCCESSED:
                    event.replySuccess("设置@提醒等级成功：\n```" + LiveEvent.getTextByMask(mask, "@提醒") + "```");
                    break;
                case OBSERVER_NOT_FOUND:
                    event.replyError("要设置@提醒等级的**" + args[0] + "@" + args[1] + "**直播间不存在");
                    break;
                default:
                    event.reply("设置@提醒等级失败");
            }
        }

        public void setMessageLevel(CommandEvent event, String[] args, String level) {
            int mask = LiveEvent.levelToMask(Integer.parseInt(level));
            switch (bot.getObManager().setMessageLevel(event.getChannel().getId(), args[0], args[1], mask)) {
                case SUCCESSED:
                    event.replySuccess("设置订阅等级成功：\n```" + LiveEvent.getTextByMask(mask, "提醒") + "```");
                    break;
                case OBSERVER_NOT_FOUND:
                    event.replyError("要设置订阅等级的**" + args[0] + "@" + args[1] + "**直播间不存在");
                    break;
                default:
                    event.reply("设置提醒等级失败");
            }
        }

        public void setMessageMask(CommandEvent event, String[] args, String mask) {
            int binMask = LiveEvent.stringToMask(mask);
            switch (bot.getObManager().setMessageMask(event.getChannel().getId(), args[0], args[1], binMask)) {
                case SUCCESSED:
                    event.replySuccess("自定义订阅Mask成功：\n```" + LiveEvent.getTextByMask(binMask, "提醒") + "```");
                    break;
                case OBSERVER_NOT_FOUND:
                    event.replyError("要设置订阅Mask的**" + args[0] + "@" + args[1] + "**直播间不存在");
                    break;
                default:
                    event.reply("设置消息等级失败");
            }
        }

        public void setMentionMask(CommandEvent event, String[] args, String mask) {
            int binMask = LiveEvent.stringToMask(mask);
            switch (bot.getObManager().setMentionMask(event.getChannel().getId(), args[0], args[1], binMask)) {
                case SUCCESSED:
                    event.replySuccess("自定义@提醒Mask成功：\n```" + LiveEvent.getTextByMask(binMask, "@提醒") + "```");
                    break;
                case OBSERVER_NOT_FOUND:
                    event.replyError("要设置@提醒Mask的**" + args[0] + "@" + args[1] + "**直播间不存在");
                    break;
                default:
                    event.reply("设置提醒等级失败");
            }
        }
    }
}
