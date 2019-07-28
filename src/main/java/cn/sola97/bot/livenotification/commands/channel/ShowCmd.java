package cn.sola97.bot.livenotification.commands.channel;

import static cn.sola97.bot.livenotification.BotConfig.*;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Menu;
import cn.sola97.bot.livenotification.Bot;
import cn.sola97.bot.livenotification.commands.ChannelCommand;
import cn.sola97.bot.livenotification.utils.ParseUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.utils.Checks;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ShowCmd extends ChannelCommand {
    private final Paginator.Builder builder;
    public ShowCmd(Bot bot) {
        super(bot);
        this.name = "show";
        this.help = "show specific page by page number ";
        this.arguments = "[pagenum]";
        this.aliases = new String[]{"list", "ls"};
        builder = new Paginator.Builder()
                .setColumns(1)
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue();
                    } catch (PermissionException ignore) {
                    }
                })
                .setText("")
                .waitOnSinglePage(true)
                .setBot(bot)
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    protected void execute(CommandEvent event) {
        int pagenum = 1;
        try {
            pagenum = Integer.parseInt(event.getArgs());
        } catch (NumberFormatException ignore) {
        }
        // use Unirest to poll an API
        List<EmbedBuilder> items = bot.getObManager().getObserversEmbedBuilderByChannelId(event.getChannel().getId());
        if (items.isEmpty()) {
            event.reply("您还没有订阅");
            return;
        }
        builder.setItems(items)
                .setColor(event.getSelfMember().getColor())
                .setUsers(event.getAuthor());
        builder.build().paginate(event.getChannel(), pagenum);
    }
}

//魔改 com.jagrosh.jdautilities.menu.Paginator
class Paginator extends Menu {
    private final BiFunction<Integer, Integer, Color> color;
    private final BiFunction<Integer, Integer, String> text;
    private final int columns;
    private final int itemsPerPage;
    private final boolean showPageNumbers;
    private final boolean numberItems;
    private final List<EmbedBuilder> embedBuilders;
    private final int pages;
    private final Consumer<Message> finalAction;
    private final boolean waitOnSinglePage;
    private final int bulkSkipNumber;
    private final boolean wrapPageEnds;
    private final String leftText;
    private final String rightText;
    private final Bot bot;
    private final boolean allowTextInput;

    Paginator(EventWaiter waiter, Set<User> users, Set<Role> roles, long timeout, TimeUnit unit, BiFunction<Integer, Integer, Color> color, BiFunction<Integer, Integer, String> text, Consumer<Message> finalAction, int columns, int itemsPerPage, boolean showPageNumbers, boolean numberItems, List<EmbedBuilder> items, boolean waitOnSinglePage, int bulkSkipNumber, boolean wrapPageEnds, String leftText, String rightText, boolean allowTextInput, Bot bot) {
        super(waiter, users, roles, timeout, unit);
        this.bot = bot;
        this.color = color;
        this.text = text;
        this.columns = columns;
        this.itemsPerPage = itemsPerPage;
        this.showPageNumbers = showPageNumbers;
        this.numberItems = numberItems;
        this.embedBuilders = items;
        this.pages = (int) Math.ceil((double) this.embedBuilders.size() / (double) itemsPerPage);
        this.finalAction = finalAction;
        this.waitOnSinglePage = waitOnSinglePage;
        this.bulkSkipNumber = bulkSkipNumber;
        this.wrapPageEnds = wrapPageEnds;
        this.leftText = leftText;
        this.rightText = rightText;
        this.allowTextInput = allowTextInput;
    }


    public void display(MessageChannel channel) {
        this.paginate((MessageChannel) channel, 1);
    }

    public void display(Message message) {
        this.paginate((Message) message, 1);
    }

    public void paginate(MessageChannel channel, int pageNum) {
        if (pageNum < 1) {
            pageNum = 1;
        } else if (pageNum > this.pages) {
            pageNum = this.pages;
        }

        Message msg = this.renderPageLoading(pageNum);
        this.initialize(channel.sendMessage(msg), pageNum);
    }

    public void paginate(Message message, int pageNum) {
        if (pageNum < 1) {
            pageNum = 1;
        } else if (pageNum > this.pages) {
            pageNum = this.pages;
        }

        Message msg = this.renderPage(pageNum);
        this.initialize(message.editMessage(msg), pageNum);
    }

    private void initialize(RestAction<Message> action, int pageNum) {
        action.queue((m) -> {
            if (this.pages > 1) {
                if (this.bulkSkipNumber > 1) {
                    m.addReaction(BIG_LEFT).queue();
                }

                m.addReaction(LEFT).queue();
                m.addReaction(STOP).queue();
                if (this.bulkSkipNumber > 1) {
                    m.addReaction(RIGHT).queue();
                }

                m.addReaction(this.bulkSkipNumber > 1 ? BIG_RIGHT : RIGHT).queue();
                m.addReaction(SUBSCRIBE).queue();
                m.addReaction(UNSUBSCRIBE).queue();
                m.addReaction(MENTION).queue();
                m.addReaction(REMOVE_MENTION).queue();
                m.addReaction(CHANGE_MENTION_LEVEL).queue();
                m.addReaction(CHANGE_MSG_LEVEL).queue();
                m.addReaction(DELETE).queue((v) -> {
                    m.editMessage(renderPage(pageNum)).queue();
                    this.pagination(m, pageNum);
                }, (t) -> {
                    m.editMessage(renderPage(pageNum)).queue();
                    this.pagination(m, pageNum);
                });
            } else if (this.waitOnSinglePage) {
                m.addReaction(STOP).queue();
                m.addReaction(SUBSCRIBE).queue();
                m.addReaction(UNSUBSCRIBE).queue();
                m.addReaction(MENTION).queue();
                m.addReaction(REMOVE_MENTION).queue();
                m.addReaction(CHANGE_MENTION_LEVEL).queue();
                m.addReaction(CHANGE_MSG_LEVEL).queue();
                m.addReaction(DELETE).queue((v) -> {
                    m.editMessage(renderPage(pageNum)).queue();
                    this.paginationWithoutTextInput(m, pageNum);
                }, (t) -> {
                    m.editMessage(renderPage(pageNum)).queue();
                    this.paginationWithoutTextInput(m, pageNum);
                });
            } else {
                this.finalAction.accept(m);
            }
        });
    }

    private void pagination(Message message, int pageNum) {
        if (!this.allowTextInput && (this.leftText == null || this.rightText == null)) {
            this.paginationWithoutTextInput(message, pageNum);
        } else {
            this.paginationWithTextInput(message, pageNum);
        }

    }

    private void paginationWithTextInput(Message message, int pageNum) {
        this.waiter.waitForEvent(GenericMessageEvent.class, (event) -> {
            if (event instanceof MessageReactionAddEvent) {
                return this.checkReaction((MessageReactionAddEvent) event, message.getIdLong());
            } else {
                if (event instanceof MessageReceivedEvent) {
                    MessageReceivedEvent mre = (MessageReceivedEvent) event;
                    if (!mre.getChannel().equals(message.getChannel())) {
                        return false;
                    }

                    String rawContent = mre.getMessage().getContentRaw().trim();
                    if (this.leftText != null && this.rightText != null && (rawContent.equalsIgnoreCase(this.leftText) || rawContent.equalsIgnoreCase(this.rightText))) {
                        return this.isValidUser(mre.getAuthor(), mre.getGuild());
                    }

                    if (this.allowTextInput) {
                        try {
                            int i = Integer.parseInt(rawContent);
                            if (1 <= i && i <= this.pages && i != pageNum) {
                                return this.isValidUser(mre.getAuthor(), mre.getGuild());
                            }
                        } catch (NumberFormatException var7) {
                        }
                    }
                }

                return false;
            }
        }, (event) -> {
            if (event instanceof MessageReactionAddEvent) {
                this.handleMessageReactionAddAction((MessageReactionAddEvent) event, message, pageNum);
            } else {
                MessageReceivedEvent mre = (MessageReceivedEvent) event;
                String rawContent = mre.getMessage().getContentRaw().trim();
                int targetPage;
                if (this.leftText == null || !rawContent.equalsIgnoreCase(this.leftText) || 1 >= pageNum && !this.wrapPageEnds) {
                    if (this.rightText == null || !rawContent.equalsIgnoreCase(this.rightText) || pageNum >= this.pages && !this.wrapPageEnds) {
                        targetPage = Integer.parseInt(rawContent);
                    } else {
                        targetPage = pageNum + 1 > this.pages && this.wrapPageEnds ? 1 : pageNum + 1;
                    }
                } else {
                    targetPage = pageNum - 1 < 1 && this.wrapPageEnds ? this.pages : pageNum - 1;
                }

                message.editMessage(this.renderPage(targetPage)).queue((m) -> {
                    this.pagination(m, targetPage);
                });
                mre.getMessage().delete().queue((v) -> {
                }, (t) -> {
                });
            }

        }, this.timeout, this.unit, () -> {
            this.finalAction.accept(message);
        });
    }

    private void paginationWithoutTextInput(Message message, int pageNum) {
        this.waiter.waitForEvent(MessageReactionAddEvent.class, (event) -> {
            return this.checkReaction(event, message.getIdLong());
        }, (event) -> {
            this.handleMessageReactionAddAction(event, message, pageNum);
        }, this.timeout, this.unit, () -> {
            this.finalAction.accept(message);
        });
    }

    private boolean checkReaction(MessageReactionAddEvent event, long messageId) {
        if (event.getMessageIdLong() != messageId) {
            return false;
        } else {
            String emoji = event.getReactionEmote().getName();
            switch (emoji) {
                case BIG_RIGHT:
                case BIG_LEFT:
                    return this.bulkSkipNumber > 1 && this.isValidUser(event.getUser(), event.getGuild());
                case LEFT:
                case RIGHT:
                case STOP:
                case SUBSCRIBE:
                case UNSUBSCRIBE:
                case MENTION:
                case DELETE:
                case REMOVE_MENTION:
                case CHANGE_MENTION_LEVEL:
                case CHANGE_MSG_LEVEL:
                    return this.isValidUser(event.getUser(), event.getGuild());
                default:
                    return false;
            }
        }
    }

    private void handleMessageReactionAddAction(MessageReactionAddEvent event, Message message, int pageNum) {
        int newPageNum = pageNum;
        int i;
        String url1 = Optional.ofNullable(embedBuilders.get(pageNum - 1).build()).map(MessageEmbed::getAuthor).map(MessageEmbed.AuthorInfo::getUrl).orElse("");
        String[] args = ParseUtil.parseUrl(url1);
        String emoji = event.getReaction().getReactionEmote().getName();
        switch (emoji) {
            case LEFT:
                if (pageNum == 1 && this.wrapPageEnds) {
                    newPageNum = this.pages + 1;
                }

                if (newPageNum > 1) {
                    --newPageNum;
                }
                break;
            case RIGHT:
                if (pageNum == this.pages && this.wrapPageEnds) {
                    newPageNum = 0;
                }

                if (newPageNum < this.pages) {
                    ++newPageNum;
                }
                break;
            case BIG_LEFT:
                if (pageNum > 1 || this.wrapPageEnds) {
                    for (i = 1; (newPageNum > 1 || this.wrapPageEnds) && i < this.bulkSkipNumber; ++i) {
                        if (newPageNum == 1 && this.wrapPageEnds) {
                            newPageNum = this.pages + 1;
                        }

                        --newPageNum;
                    }
                }
                break;
            case BIG_RIGHT:
                if (pageNum < this.pages || this.wrapPageEnds) {
                    for (i = 1; (newPageNum < this.pages || this.wrapPageEnds) && i < this.bulkSkipNumber; ++i) {
                        if (newPageNum == this.pages && this.wrapPageEnds) {
                            newPageNum = 0;
                        }

                        ++newPageNum;
                    }
                }
                break;
            case SUBSCRIBE:
                if (args.length == 2) {
                    switch (bot.getObManager().subscribe(event.getChannel().getId(), args[0], args[1])) {
                        case SUCCESSED:
                            EmbedBuilder mb = bot.getObManager().getObserver(event.getChannel().getId(), args[0], args[1]).get().getEmbedBuilderShowAll();
                            embedBuilders.set(pageNum - 1, mb);
                            break;
                        case ALREADY_SUBSCRIBED:
                            break;
                        case INVALID_USER:
                            event.getChannel().sendMessage("订阅的用户**" + args[0] + "@" + args[1] + "**不存在").queue();
                        case FAILED:
                            event.getChannel().sendMessage("订阅**" + args[0] + "@" + args[1] + "**失败").queue();
                            break;
                        default:
                            event.getChannel().sendMessage("订阅**" + args[0] + "@" + args[1] + "**结果未知").queue();
                    }
                }
                break;
            case UNSUBSCRIBE:
                if (args.length == 2)
                    switch (bot.getObManager().unsubscribe(event.getChannel().getId(), args[0], args[1])) {
                        case SUCCESSED:
                            EmbedBuilder mb = bot.getObManager().getObserver(event.getChannel().getId(), args[0], args[1]).get().getEmbedBuilderShowAll();
                            embedBuilders.set(pageNum - 1, mb);
                            break;
                        case ALREADY_UNSUBSCRIBED:
                            break;
                        case FAILED:
                            event.getChannel().sendMessage("取消订阅**" + args[0] + "@" + args[1] + "**失败").queue();
                            break;
                        default:
                            event.getChannel().sendMessage("取消订阅**" + args[0] + "@" + args[1] + "**结果未知").queue();
                    }
                break;
            case DELETE:
                if (args.length == 2)
                    switch (bot.getObManager().deleteObserver(event.getChannel().getId(), args[0], args[1])) {
                        case SUCCESSED:
                            embedBuilders.set(newPageNum - 1, new EmbedBuilder().setDescription("deleted").addBlankField(false).addBlankField(false));
                            break;
                        case ALREADY_DELETED:
                            event.getChannel().sendMessage("删除失败**" + args[0] + "@" + args[1] + "**不存在").queue();
                            break;
                        case FAILED:
                            event.getChannel().sendMessage("删除**" + args[0] + "@" + args[1] + "**失败").queue();
                            break;
                        default:
                            event.getChannel().sendMessage("删除**" + args[0] + "@" + args[1] + "**结果未知").queue();
                    }
                break;
            case MENTION:
                if (args.length == 2)
                    switch (bot.getObManager().addMention(event.getChannel().getId(), args[0], args[1], event.getUser().getAsMention())) {
                        case SUCCESSED:
                            EmbedBuilder mb = bot.getObManager().getObserver(event.getChannel().getId(), args[0], args[1]).get().getEmbedBuilderShowAll();
                            embedBuilders.set(pageNum - 1, mb);
                            break;
                        case OBSERVER_NOT_FOUND:
                            event.getChannel().sendMessage("添加提醒**" + args[0] + "@" + args[1] + "**没有找到直播间").queue();
                            break;
                        case ALREADY_EXISTS_MENTIONS:
                            break;
                        default:
                            event.getChannel().sendMessage("添加提醒**" + args[0] + "@" + args[1] + "**结果未知").queue();
                    }
                break;
            case REMOVE_MENTION:
                if (args.length == 2)
                    switch (bot.getObManager().removeMention(event.getChannel().getId(), args[0], args[1], event.getUser().getAsMention())) {
                        case SUCCESSED:
                            EmbedBuilder mb = bot.getObManager().getObserver(event.getChannel().getId(), args[0], args[1]).get().getEmbedBuilderShowAll();
                            embedBuilders.set(pageNum - 1, mb);
                            break;
                        case OBSERVER_NOT_FOUND:
                            event.getChannel().sendMessage("添加提醒**" + args[0] + "@" + args[1] + "**没有找到直播间").queue();
                            break;
                        case ALREADY_REMOVED_MENTIONS:
                            break;
                        default:
                            event.getChannel().sendMessage("添加提醒**" + args[0] + "@" + args[1] + "**结果未知").queue();
                    }
                break;
            case CHANGE_MENTION_LEVEL:
                if(args.length==2)
                    switch (bot.getObManager().changeMentionLevel(event.getChannel().getId(), args[0], args[1])){
                        case SUCCESSED:
                            EmbedBuilder mb = bot.getObManager().getObserver(event.getChannel().getId(), args[0], args[1]).get().getEmbedBuilderShowAll();
                            embedBuilders.set(pageNum - 1, mb);
                            break;
                        case OBSERVER_NOT_FOUND:
                            event.getChannel().sendMessage("要设置@提醒等级的**" + args[0] + "@" + args[1] + "**直播间不存在").queue();
                            break;
                        default:
                            event.getChannel().sendMessage("设置提醒等级结果未知").queue();
                            break;
                    }
                break;
            case CHANGE_MSG_LEVEL:
                if(args.length==2)
                    switch (bot.getObManager().changeMessageLevel(event.getChannel().getId(), args[0], args[1])){
                        case SUCCESSED:
                            EmbedBuilder mb = bot.getObManager().getObserver(event.getChannel().getId(), args[0], args[1]).get().getEmbedBuilderShowAll();
                            embedBuilders.set(pageNum - 1, mb);
                            break;
                        case OBSERVER_NOT_FOUND:
                            event.getChannel().sendMessage("要设置订阅等级的**" + args[0] + "@" + args[1] + "**直播间不存在").queue();
                            break;
                        default:
                            event.getChannel().sendMessage("设置订阅等级结果未知").queue();
                            break;
                    }
                break;
            case STOP:
                this.finalAction.accept(message);
                return;
        }

        try {
            event.getReaction().removeReaction(event.getUser()).queue();
        } catch (PermissionException var8) {
        }

        int finalNewPageNum = newPageNum;
        message.editMessage(this.renderPage(newPageNum)).queue((m) -> {
            this.pagination(m, finalNewPageNum);
        });
    }

    private Message renderPage(int pageNum) {
        MessageBuilder mbuilder = new MessageBuilder();
        EmbedBuilder emb = embedBuilders.get(pageNum - 1);
        mbuilder.setEmbed(emb.build());
        mbuilder.setContent("Page " + pageNum + "/" + this.pages);

        return mbuilder.build();
    }

    private Message renderPageLoading(int pageNum) {
        MessageBuilder mbuilder = new MessageBuilder();
        EmbedBuilder emb = embedBuilders.get(pageNum - 1);
        mbuilder.setEmbed(emb.build());
        mbuilder.setContent("\uD83D\uDEABLoading...");
        return mbuilder.build();
    }

    public static class Builder extends com.jagrosh.jdautilities.menu.Menu.Builder<Paginator.Builder, Paginator> {
        private BiFunction<Integer, Integer, Color> color = (page, pages) -> {
            return null;
        };
        private BiFunction<Integer, Integer, String> text = (page, pages) -> {
            return null;
        };
        private Consumer<Message> finalAction = (m) -> {
            m.delete().queue();
        };
        private int columns = 1;
        private int itemsPerPage = 1;
        private boolean showPageNumbers = true;
        private boolean numberItems = false;
        private boolean waitOnSinglePage = false;
        private int bulkSkipNumber = 1;
        private boolean wrapPageEnds = false;
        private String textToLeft = null;
        private String textToRight = null;
        private boolean allowTextInput = false;
        private Bot bot;
        private final List<EmbedBuilder> embedBuilders = new ArrayList<>();

        public Builder() {
        }

        public Paginator build() {
            Checks.check(this.waiter != null, "Must set an EventWaiter");
            Checks.check(!this.embedBuilders.isEmpty(), "Must include at least one item to paginate");
            return new Paginator(this.waiter, this.users, this.roles, this.timeout, this.unit, this.color, this.text, this.finalAction, this.columns, this.itemsPerPage, this.showPageNumbers, this.numberItems, this.embedBuilders, this.waitOnSinglePage, this.bulkSkipNumber, this.wrapPageEnds, this.textToLeft, this.textToRight, this.allowTextInput, this.bot);
        }

        public Paginator.Builder setColor(Color color) {
            this.color = (i0, i1) -> {
                return color;
            };
            return this;
        }

        public Paginator.Builder setColor(BiFunction<Integer, Integer, Color> colorBiFunction) {
            this.color = colorBiFunction;
            return this;
        }

        public Paginator.Builder setText(String text) {
            this.text = (i0, i1) -> {
                return text;
            };
            return this;
        }

        public Paginator.Builder setText(BiFunction<Integer, Integer, String> textBiFunction) {
            this.text = textBiFunction;
            return this;
        }

        public Paginator.Builder setBot(Bot bot) {
            this.bot = bot;
            return this;
        }

        public Paginator.Builder setFinalAction(Consumer<Message> finalAction) {
            this.finalAction = finalAction;
            return this;
        }

        public Paginator.Builder setColumns(int columns) {
            if (columns >= 1 && columns <= 3) {
                this.columns = columns;
                return this;
            } else {
                throw new IllegalArgumentException("Only 1, 2, or 3 columns are supported");
            }
        }

        public Paginator.Builder setItemsPerPage(int num) {
            throw new UnsupportedOperationException("只能显示一页");
        }


        public Paginator.Builder useNumberedItems(boolean number) {
            this.numberItems = number;
            return this;
        }

        public Paginator.Builder waitOnSinglePage(boolean wait) {
            this.waitOnSinglePage = wait;
            return this;
        }

        public Paginator.Builder clearItems() {
            this.embedBuilders.clear();
            return this;
        }

        public Paginator.Builder addItems(List<EmbedBuilder> items) {
            this.embedBuilders.addAll(items);
            return this;
        }

        public Paginator.Builder setItems(List<EmbedBuilder> items) {
            this.embedBuilders.clear();
            this.embedBuilders.addAll(items);
            return this;
        }

        public Paginator.Builder setBulkSkipNumber(int bulkSkipNumber) {
            this.bulkSkipNumber = Math.max(bulkSkipNumber, 1);
            return this;
        }

        public Paginator.Builder wrapPageEnds(boolean wrapPageEnds) {
            this.wrapPageEnds = wrapPageEnds;
            return this;
        }

        public Paginator.Builder allowTextInput(boolean allowTextInput) {
            this.allowTextInput = allowTextInput;
            return this;
        }

        public Paginator.Builder setLeftRightText(String left, String right) {
            if (left != null && right != null) {
                this.textToLeft = left;
                this.textToRight = right;
            } else {
                this.textToLeft = null;
                this.textToRight = null;
            }

            return this;
        }
    }
}
