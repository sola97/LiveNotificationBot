package cn.sola97.bot.livenotification.observable;

import cn.sola97.bot.livenotification.api.TwitchAPI;
import cn.sola97.bot.livenotification.enums.LiveEvent;
import cn.sola97.bot.livenotification.enums.LiveStatus;
import cn.sola97.bot.livenotification.pojo.LiveDTO;
import cn.sola97.bot.livenotification.pojo.impl.TwitchDTO;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TwitchObservable extends BaseObservable {
    private String userId;

    public TwitchObservable(String userId) {
        super();
        this.userId = userId;

    }

    @Override
    protected String getLiveId() {
        return userId;
    }

    @Override
    public Boolean diff(LiveDTO newDTO) {
            //如果正在直播,则可以获取到channel信息
            if (keepedDTO == null && newDTO.getLiveStatus()== LiveStatus.OPENED) {
                keepedDTO = newDTO.setLiveEvent(LiveEvent.INIT);
                setChanged(true);
            } else if(keepedDTO == null){
            //如果不在直播,则没有channel数据,调用getChannel更新基本信息
                try {
                    Optional<TwitchDTO> dto = TwitchAPI.getChannel(userId).get();
                    if(dto.isPresent()){
                        keepedDTO = dto.get()
                                    .setLiveEvent(LiveEvent.INIT)
                                    .setLiveStatus(newDTO.getLiveStatus()); //更新默认值unknown
                        setChanged(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (keepedDTO.getLiveStatus() == LiveStatus.CLOSED && newDTO.getLiveStatus() == LiveStatus.OPENED) {
                //更新keepedDTO信息
                newDTO.setLiveEvent(LiveEvent.OPEN);
                keepedDTO = newDTO;
                setChanged(true);
            }//opened -> closed
            else if (keepedDTO.getLiveStatus() == LiveStatus.OPENED && newDTO.getLiveStatus() == LiveStatus.CLOSED) {
                keepedDTO.setLiveEvent(LiveEvent.CLOSE);
                //没有Channel数据,只更新keepedDTO的status
                keepedDTO.setLiveStatus(LiveStatus.CLOSED);
                setChanged(true);
            }//(title|profile)changed with opened
            else if (keepedDTO.getLiveStatus() == LiveStatus.OPENED && newDTO.getLiveStatus() == LiveStatus.OPENED) {
                //直播中可以获取Channel数据，更新keeped
                if (!keepedDTO.getTitle().equals(newDTO.getTitle()) && !newDTO.getTitle().equals("null")) {
                    newDTO.setLiveEvent(LiveEvent.TITLE_CHANGED);
                    keepedDTO = newDTO;
                    setChanged(true);
                }else if (!Objects.equals(keepedDTO.getThumbnail(), newDTO.getThumbnail()) && newDTO.getThumbnail() != null) {
                    newDTO.setLiveEvent(LiveEvent.THUMBNAIL_CHANGED);
                    keepedDTO = newDTO;
                    setChanged(true);
                }
        }
        return changed;
    }

    @Override
    public CompletableFuture<Optional<TwitchDTO>> getPage() {
        return TwitchAPI.getStreams(userId);
    }
}
