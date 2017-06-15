package com.example.he.zzulimusic.bean;

import java.io.Serializable;

/**
 * Created by he on 2017/2/28.
 */

public class Mp3Info implements Serializable {
    private long id;//歌曲ID
    private String title;//歌曲名称
    private String album;//专辑
    private long albumId;//专辑ID
    private String displayName;//显示名称
    private String artist;//歌手名称
    private long duration;//歌曲市场
    private long size;//歌曲大小
    private String url;//歌曲路径
    private String IrcTitle;//歌词名称
    private String IrcSize;//歌词大小
    private String picUri;//用于网络歌曲
    private int isMusic;
    private boolean loveMusic;
    private String lrcLink;
    private String bigPicUri;

    public Mp3Info() {
        super();
    }

    public Mp3Info(long id, String title, String album, long albumId, String displayName, String artist, long duration, long size, String url, String IrcTitle, String IrcSize) {
        super();
        this.id = id;
        this.title = title;
        this.album = album;
        this.albumId = albumId;
        this.displayName = displayName;
        this.artist = artist;
        this.duration = duration;
        this.size = size;
        this.url = url;
        this.IrcTitle = IrcTitle;
        this.IrcSize = IrcSize;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIrcTitle() {
        return IrcTitle;
    }

    public void setIrcTitle(String ircTitle) {
        IrcTitle = ircTitle;
    }

    public String getIrcSize() {
        return IrcSize;
    }

    public void setIrcSize(String ircSize) {
        IrcSize = ircSize;
    }

    public String getPicUri() {
        return picUri;
    }

    public void setPicUri(String picUri) {
        this.picUri = picUri;
    }

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }

    public void setLoveMusic(boolean loveMusic) {
        this.loveMusic = loveMusic;
    }

    public String getLrcLink() {
        return lrcLink;
    }

    public void setLrcLink(String lrcLink) {
        this.lrcLink = lrcLink;
    }

    public boolean isLoveMusic() {
        return loveMusic;
    }

    public String getBigPicUri() {
        return bigPicUri;
    }

    public void setBigPicUri(String bigPicUri) {
        this.bigPicUri = bigPicUri;
    }

}
