package cn.sola97.bot.livenotification.pojo.impl;

import cn.sola97.bot.livenotification.enums.LiveEvent;
import cn.sola97.bot.livenotification.enums.LiveStatus;
import cn.sola97.bot.livenotification.pojo.LiveDTO;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class BilibiliDTO implements LiveDTO, Serializable {

    private String room_id;
    private String title;
    private String cover;
    private String uname;
    private String face;
    private String m_face;
    private String status;
    private String area;
    private String area_v2_name;
    private String area_v2_parent_name;
    private LiveStatus liveStatus = LiveStatus.UNKNOWN;
    private LiveEvent liveEvent = LiveEvent.NONE;


    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public void setM_face(String m_face) {
        this.m_face = m_face;
    }

    public void setStatus(String status) {
        this.status = status;
        switch (status) {
            case "PREPARING":
                liveStatus = LiveStatus.CLOSED;
                break;
            case "ROUND":
                liveStatus = LiveStatus.ROUNDED;
                break;
            case "LIVE":
                liveStatus = LiveStatus.OPENED;
                break;
            default:
                liveStatus = LiveStatus.UNKNOWN;
        }
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setArea_v2_name(String area_v2_name) {
        this.area_v2_name = area_v2_name;
    }

    public void setArea_v2_parent_name(String area_v2_parent_name) {
        this.area_v2_parent_name = area_v2_parent_name;
    }

    public BilibiliDTO setLiveStatus(LiveStatus liveStatus) {
        this.liveStatus = liveStatus;
        return this;
    }

    @Override
    public BilibiliDTO setLiveEvent(LiveEvent event) {
        this.liveEvent = event;
        return this;
    }


    @NotNull
    @Override
    public LiveEvent getLiveEvent() {
        return liveEvent;
    }


    @NotNull
    @Override
    public String getTitle() {
        return Optional.ofNullable(title).orElse("null");
    }

    @Override
    public String getUserName() {
        return uname;
    }

    @Override
    public String getArea() {
        if (!area_v2_parent_name.isEmpty() && !area_v2_name.isEmpty())
            return area_v2_name + " " + area_v2_parent_name;
        else if (!area.isEmpty()) {
            return area;
        } else
            return null;
    }

    @Override
    public String getVideoUrl() {
        return null;
    }

    @Override
    public String getChannelUrl() {
        if (room_id != null)
            return "https://live.bilibili.com/" + room_id;
        return null;
    }

    @Override
    public String getProfile() {
        return m_face;
    }

    @Override
    public String getThumbnail() {
        return cover;
    }

    @NotNull
    @Override
    public LiveStatus getLiveStatus() {
        return liveStatus;
    }


    @Override
    public String toString() {
        return "BilibiliDTO{" +
                "room_id='" + room_id + '\'' +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", uname='" + uname + '\'' +
                ", face='" + face + '\'' +
                ", m_face='" + m_face + '\'' +
                ", status='" + status + '\'' +
                ", area='" + area + '\'' +
                ", area_v2_name='" + area_v2_name + '\'' +
                ", area_v2_parent_name='" + area_v2_parent_name + '\'' +
                ", liveStatus=" + liveStatus +
                ", liveEvent=" + liveEvent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BilibiliDTO that = (BilibiliDTO) o;
        return Objects.equals(room_id, that.room_id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(cover, that.cover) &&
                Objects.equals(uname, that.uname) &&
                Objects.equals(face, that.face) &&
                Objects.equals(m_face, that.m_face) &&
                Objects.equals(status, that.status) &&
                Objects.equals(area, that.area) &&
                Objects.equals(area_v2_name, that.area_v2_name) &&
                Objects.equals(area_v2_parent_name, that.area_v2_parent_name) &&
                liveStatus == that.liveStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(room_id, title, cover, uname, face, m_face, status, area, area_v2_name, area_v2_parent_name, liveStatus);
    }


}
