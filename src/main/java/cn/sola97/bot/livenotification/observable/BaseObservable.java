package cn.sola97.bot.livenotification.observable;

import cn.sola97.bot.livenotification.enums.CommandResults;
import cn.sola97.bot.livenotification.enums.LiveEvent;
import cn.sola97.bot.livenotification.enums.LiveStatus;
import cn.sola97.bot.livenotification.observer.BaseObserver;
import cn.sola97.bot.livenotification.pojo.LiveDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class BaseObservable implements Runnable, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(BaseObservable.class);
    LiveDTO keepedDTO;
    private List<BaseObserver> observers;
    protected Boolean changed = false;

    protected abstract String getLiveId();

    protected abstract <T extends LiveDTO> CompletableFuture<Optional<LiveDTO>> getPage();

    BaseObservable() {
        this.observers = Collections.synchronizedList(new ArrayList<>());
    }


    public void setChanged(Boolean changed) {
        this.changed = changed;
    }

    private String getClassName() {
        return String.format(this.getClass().getSimpleName() + "@[%s]", Optional.ofNullable(keepedDTO).map(LiveDTO::getUserName).orElse(getLiveId()));
    }


    public CommandResults addObserver(BaseObserver o) {
        if (observers.contains(o)) return CommandResults.ALREADY_SUBSCRIBED;
        observers.add(o);
        logger.info(getClassName() + "  添加 - " + o.getClassName() + "  Total：" + observers.size());
        if (keepedDTO != null)
            o.update(keepedDTO.setLiveEvent(LiveEvent.NONE));
        return CommandResults.SUCCESSED;
    }

    public CommandResults removeObserver(BaseObserver o) {
        if (observers.contains(o)) {
            observers.remove(o);
            logger.info(getClassName() + "  移除 - " + o.getClassName() + "  Total：" + observers.size());
            return CommandResults.SUCCESSED;
        } else {
            return CommandResults.ALREADY_UNSUBSCRIBED;
        }
    }

    private void notifyObservers(LiveDTO dto) {
        if (changed)
            for (BaseObserver observer : observers) {
                observer.update(dto);
            }
        setChanged(false);
    }

    protected Boolean diff(LiveDTO newDTO) {
        if (keepedDTO == null) {
            newDTO.setLiveEvent(LiveEvent.INIT);
            keepedDTO = newDTO;
            setChanged(true);
        } else if (keepedDTO.getLiveStatus() == LiveStatus.CLOSED && newDTO.getLiveStatus() == LiveStatus.OPENED) {
            newDTO.setLiveEvent(LiveEvent.OPEN);
            keepedDTO = newDTO;
            setChanged(true);
        } else if (keepedDTO.getLiveStatus() == LiveStatus.OPENED && newDTO.getLiveStatus() == LiveStatus.CLOSED) {
            newDTO.setLiveEvent(LiveEvent.CLOSE);
            keepedDTO = newDTO;
            setChanged(true);
        } else if (!keepedDTO.getTitle().equals(newDTO.getTitle()) && !newDTO.getTitle().equals("null")) {
            newDTO.setLiveEvent(LiveEvent.TITLE_CHANGED);
            keepedDTO = newDTO;
            setChanged(true);
        } else if (!Objects.equals(keepedDTO.getThumbnail(), newDTO.getThumbnail()) && newDTO.getThumbnail() != null) {
            newDTO.setLiveEvent(LiveEvent.THUMBNAIL_CHANGED);
            keepedDTO = newDTO;
            setChanged(true);
        }
        return changed;
    }

    @Override
    public void run() {
        try {
            if (observers.size() != 0)
                getPage().thenAccept(dto -> {
                    if (dto.isPresent() && diff(dto.get())) {
                        logger.info(getClassName() + "  通知所有Observers");
                        notifyObservers(keepedDTO);
                    } else if (!dto.isPresent()) {
                        logger.info(getClassName() + "  Room数据不存在");
                    } else {
                        logger.info(getClassName() + "  没有变化");
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
