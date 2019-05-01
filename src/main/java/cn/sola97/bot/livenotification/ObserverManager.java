package cn.sola97.bot.livenotification;


import cn.sola97.bot.livenotification.api.BaseAPI;
import cn.sola97.bot.livenotification.enums.CommandResults;
import cn.sola97.bot.livenotification.observable.BaseObservable;
import cn.sola97.bot.livenotification.observable.BilibiliObservable;
import cn.sola97.bot.livenotification.observable.TwitchObservable;
import cn.sola97.bot.livenotification.observable.YoutubeObservable;
import cn.sola97.bot.livenotification.observer.BaseObserver;
import cn.sola97.bot.livenotification.observer.BilibiliObserver;
import cn.sola97.bot.livenotification.observer.TwitchObserver;
import cn.sola97.bot.livenotification.observer.YoutubeObserver;
import net.dv8tion.jda.core.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ObserverManager implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ObserverManager.class);
    private static Map<String, BiFunction<String, String, BaseObserver>> observerFactory;
    private static Map<String, Function<String, BaseObservable>> observableFactory;
    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();


    static {
        Map<String, BiFunction<String, String, BaseObserver>> tempMap = new HashMap<>();
        tempMap.put("bilibili", BilibiliObserver::new);
        tempMap.put("twitch", TwitchObserver::new);
        tempMap.put("youtube", YoutubeObserver::new);
        observerFactory = Collections.unmodifiableMap(tempMap);

        HashMap<String, Function<String, BaseObservable>> tempMap2 = new HashMap<>();
        tempMap2.put("bilibili", BilibiliObservable::new);
        tempMap2.put("twitch", TwitchObservable::new);
        tempMap2.put("youtube", YoutubeObservable::new);
        observableFactory = Collections.unmodifiableMap(tempMap2);

    }

    //channel/platform/user
    private ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, BaseObserver>>> observerPool;
    //platform/user
    private ConcurrentHashMap<String, ConcurrentHashMap<String, BaseObservable>> observablePool;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Future<?>>> scheduledPool;

    ObserverManager() {
        load();
    }

    public CommandResults subscribe(String channelId, String type, String key) {
        try {
            if (BaseAPI.checkValid(type, key).get() == CommandResults.INVALID_USER)
                return CommandResults.INVALID_USER;
            Optional<BaseObservable> opObservable = getObservable(type, key);
            Optional<BaseObserver> opObserver = getObserver(channelId, type, key);
            if (opObservable.isPresent() && opObserver.isPresent()) {
                CommandResults res = opObservable.get().addObserver(opObserver.get().setEnabled(true));
                if (res == CommandResults.SUCCESSED) {
                    save();
                }
                return res;
            } else {
                BaseObservable observable = getObservableElseNew(type, key);
                BaseObserver observer = getObserverElseNew(channelId, type, key).setEnabled(true);
                CommandResults res = observable.addObserver(observer);
                if (res == CommandResults.SUCCESSED) {
                    addToShedulePool(observable, type, key);
                    save();
                }
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CommandResults.FAILED;
        }
    }

    public CommandResults unsubscribe(String channelId, String type, String key) {

        Optional<BaseObservable> observable = getObservable(type, key);
        Optional<BaseObserver> observer = getObserver(channelId, type, key);
        if (observable.isPresent() && observer.isPresent()) {
            CommandResults res = observable.get().removeObserver(observer.get().setEnabled(false));
            if (res == CommandResults.SUCCESSED)
                save();
            return res;
        }
        return CommandResults.OBSERVER_NOT_FOUND;
    }

    public CommandResults setMessageMask(String channelId, String type, String key, int mask) {
        return getObserver(channelId, type, key)
                .map(ob -> ob.setMessageMask(mask))
                .orElse(CommandResults.OBSERVER_NOT_FOUND);
    }

    public CommandResults setMentionMask(String channelId, String type, String key, int mask) {
        return getObserver(channelId, type, key)
                .map(ob -> ob.setMentionMask(mask))
                .orElse(CommandResults.OBSERVER_NOT_FOUND);
    }
    public CommandResults setMessageLevel(String channelId, String type, String key, int mask) {
        return getObserver(channelId, type, key)
                .map(ob -> ob.setMessageLevelMask(mask))
                .orElse(CommandResults.OBSERVER_NOT_FOUND);
    }

    public CommandResults setMentionLevel(String channelId, String type, String key, int mask) {
        return getObserver(channelId, type, key)
                .map(ob -> ob.setMentionLevelMask(mask))
                .orElse(CommandResults.OBSERVER_NOT_FOUND);
    }
    public CommandResults changeMessageLevel(String channelId, String type, String key) {
        return getObserver(channelId, type, key)
                .map(ob -> ob.changeMessageLevel())
                .orElse(CommandResults.OBSERVER_NOT_FOUND);
    }
    public CommandResults changeMentionLevel(String channelId, String type, String key) {
        return getObserver(channelId, type, key)
                .map(ob -> ob.changeMentionLevel())
                .orElse(CommandResults.OBSERVER_NOT_FOUND);
    }
    public CommandResults addMentions(String channelId, String type, String key, List<String> mentions) {
        if (mentions.isEmpty()) return CommandResults.EMPTY_MENTIONS;
        return getObserver(channelId, type, key)
                .map(ob -> ob.addMentions(mentions.toArray(new String[0])))
                .orElse(CommandResults.OBSERVER_NOT_FOUND);
    }

    public CommandResults addMention(String channelId, String type, String key, String mention) {
        return getObserver(channelId, type, key)
                .map(ob -> ob.addMentions(mention))
                .orElse(CommandResults.OBSERVER_NOT_FOUND);
    }

    public CommandResults removeMention(String channelId, String type, String key, String mention) {
        return getObserver(channelId, type, key)
                .map(ob -> ob.removeMentions(mention))
                .orElse(CommandResults.OBSERVER_NOT_FOUND);
    }

    public CommandResults removeMentions(String channelId, String type, String key, List<String> mentions) {
        if (mentions.isEmpty()) return CommandResults.EMPTY_MENTIONS;
        return getObserver(channelId, type, key)
                .map(ob -> ob.removeMentions(mentions.toArray(new String[0])))
                .orElse(CommandResults.OBSERVER_NOT_FOUND);
    }

    public CommandResults clearMentions(String channelId, String type, String key) {
        return getObserver(channelId, type, key)
                .map(BaseObserver::clearMentions)
                .orElse(CommandResults.OBSERVER_NOT_FOUND);
    }

    public Set<String> getChannels() {
        return observerPool.keySet();
    }

    public List<EmbedBuilder> getObserversEmbedBuilderByChannelId(String channelId) {
        List<EmbedBuilder> descriptons = new ArrayList<>();
        try {
            Optional.ofNullable(observerPool).map(m -> m.get(channelId))
                    .ifPresent(type -> type.values().forEach(key ->
                            key.values().forEach(observer ->
                                    descriptons.add(observer.getEmbedBuilderShowAll()))));
        } catch (Exception e) {
            logger.error("channelId:" + channelId, e);
        }
        return descriptons;
    }


    public CommandResults deleteObserver(String channelId, String type, String key) {
        Optional<BaseObservable> observable = getObservable(type, key);
        Optional<BaseObserver> observer = getObserver(channelId, type, key);
        observable.ifPresent(obv -> observer.ifPresent(obv::removeObserver));
        return observer.map(o -> {
            observerPool.get(channelId).get(type).remove(key);
            save();
            return CommandResults.SUCCESSED;
        }).orElse(CommandResults.ALREADY_DELETED);
    }

    public BaseObserver getObserverElseNew(String channelId, String type, String key) {
        return observerPool.computeIfAbsent(channelId, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(type, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(key, k -> observerFactory.get(type).apply(channelId, key));
    }

    public Optional<BaseObserver> getObserver(String channelId, String type, String key) {
        return Optional.ofNullable(observerPool).map(m -> m.get(channelId)).map(c -> c.get(type)).map(t -> t.get(key));
    }


    public BaseObservable getObservableElseNew(String type, String key) {
        return observablePool.computeIfAbsent(type, k -> new ConcurrentHashMap<>()).computeIfAbsent(key, k -> observableFactory.get(type).apply(key));
    }

    public Optional<BaseObservable> getObservable(String type, String key) {
        return Optional.ofNullable(observablePool).map(m -> m.get(type)).map(t -> t.get(key));
    }

    public void addToShedulePool(BaseObservable ob, String type, String key) {
        scheduledPool.computeIfAbsent(type, k -> new ConcurrentHashMap<>()).put(key, executor.scheduleAtFixedRate(ob, 0, 10, TimeUnit.SECONDS));
    }

    public void deleteChannel(String channelId) {
        Optional.ofNullable(observerPool.get(channelId)).ifPresent(tmap-> tmap.forEach((type, kmap) -> kmap.forEach(((key, ob) -> getObservable(type, key).ifPresent(obv -> obv.removeObserver(ob))))));
        observerPool.remove(channelId);
        save();
    }

    private AtomicInteger countObserver() {
        AtomicInteger count = new AtomicInteger();
        observerPool.values().forEach(e -> e.values().forEach(e1 -> count.addAndGet(e1.values().size())));
        return count;
    }

    private AtomicInteger countObservable() {
        AtomicInteger count = new AtomicInteger();
        observablePool.values().forEach(e1 -> count.addAndGet(e1.values().size()));
        return count;
    }

    private AtomicInteger countScheduled() {
        AtomicInteger count = new AtomicInteger();
        scheduledPool.values().forEach(e1 -> count.addAndGet(e1.values().size()));
        return count;
    }

    @Override
    public String toString() {
        return "ObserverManager{" +
                "observerPool=" + countObserver() +
                ", observablePool=" + countObservable() +
                ", scheduledPool=" + countScheduled() +
                '}';
    }

    public Boolean saveObject(String fileName, Object obj) {
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream objOut = new ObjectOutputStream(fileOut)
        ) {
            objOut.writeObject(obj);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }

    public Optional<Object> loadObject(String fileName) throws IOException {
        if (!Files.exists(Paths.get(fileName)))
            return Optional.empty();
        try (
                FileInputStream fIn = new FileInputStream(fileName);
                ObjectInputStream objIn = new ObjectInputStream(fIn)
        ) {
            return Optional.of(objIn.readObject());
        } catch (Exception e) {
            Files.delete(Paths.get(fileName));
            logger.info("读取文件出错，初始化");
        }
        return Optional.empty();
    }

    private Boolean saveObserverPool() {
        String fileName = "observer.dat";
        return saveObject(fileName, observerPool);
    }

    public Boolean loadObserverPool() {
        String fileName = "observer.dat";
        try {
            Optional<Object> obj = loadObject(fileName);
            if (obj.isPresent()) {
                observerPool = (ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, BaseObserver>>>) obj.get();
                return true;
            }
        } catch (Exception e) {
            try {
                Files.deleteIfExists(Paths.get(fileName));
                logger.warn("发生错误,删除 " + fileName);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    private void scheduledPoolInit() {
        scheduledPool = new ConcurrentHashMap<>();
        observablePool.forEach((type, subMap) -> subMap.forEach((key, ob) -> addToShedulePool(ob, type, key)));
    }

    private void observablePoolInit() {
        observablePool = new ConcurrentHashMap<>();
        observerPool.forEach((channel, tmap) -> tmap.forEach((type, kmap) -> kmap.forEach((key, ob) -> getObservableElseNew(type, key).addObserver(ob))));
    }

    private synchronized void save() {
        logger.info("save observerPool");
        saveObserverPool();
    }

    private void load() {
        if (!loadObserverPool()) observerPool = new ConcurrentHashMap<>();
        observablePoolInit();
        scheduledPoolInit();
    }

    public void shutdown() {
        logger.info("shutdown - 保存observers");
        save();
        logger.info("shutdown - executor shutdown");
        executor.shutdown();
    }
}
